package auth.center.controller;

import common.core.annotation.SysLog;
import common.core.entity.RedisHeaders;
import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.entity.dto.AuthUserDTO;
import common.core.utils.GetClientIp;
import common.core.utils.JsonUtils;
import common.core.entity.dto.TokenDTO;
import common.feign.clients.UserClient;
import io.github.eternalstone.captcha.gp.base.Captcha;
import io.github.eternalstone.captcha.gp.base.TextEntry;
import io.github.eternalstone.captcha.gp.captcha.GifCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import common.security.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author abing
 * @created 2025/4/15 17:33
 */

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @SysLog(explain = "用户登录")
    @PostMapping("/login")
    public Resp<Object> login(@RequestBody AuthUserDTO authUserDTO, HttpServletRequest request) {

        if (Objects.isNull(authUserDTO)) {
            return Resp.error("用户参数为空", StatusCode.SELECT_ERR);
        }
        String checkCode = redisTemplate.opsForValue().get(RedisHeaders.CHECK_CODE_UNIQUE_ID + authUserDTO.getCheckCodeId());
//       仅测试使用
        if (!authUserDTO.getCheckCode().equals("abing")) {
            if (StringUtils.isBlank(checkCode)) {
                return Resp.error("验证码过期", StatusCode.SELECT_ERR);
            }
            if (!Objects.equals(checkCode, authUserDTO.getCheckCode())) {
                redisTemplate.delete(RedisHeaders.CHECK_CODE_UNIQUE_ID + authUserDTO.getCheckCodeId());
                return Resp.error("验证码错误", StatusCode.CHECKCODE_EXCEPTION);
            }
        }

        AuthUserDTO authVo;
        String token;
        String authenticatedCache = redisTemplate.opsForValue().get(RedisHeaders.USER_CACHE + authUserDTO.getUsername());

        if (Objects.isNull(authenticatedCache)) {
            Resp<AuthUserDTO> resp = userClient.login(authUserDTO);
            log.warn(String.valueOf(resp));
            if (!Objects.equals(resp.getCode(), StatusCode.SELECT_SUCCESS)) {
                return Resp.error(resp.getMsg(), StatusCode.SELECT_ERR);
            }
            authVo = resp.getData();
        } else {
            authVo = JsonUtils.toBean(authenticatedCache, AuthUserDTO.class);
        }

        List<String> roleList = authVo.getRoles();
        if (Objects.isNull(roleList) || roleList.isEmpty()) {
            return Resp.error("用户角色为空", StatusCode.AUTHORIZED_EXCEPTION);
        }
        List<String> permissionList = authVo.getPermissions();
        String roles = JsonUtils.toJson(roleList);
        String permissions = JsonUtils.toJson(permissionList);
        token = JwtUtil.createToken(authVo.getId(), authVo.getUsername(), roles, permissions);

//        随机uuid，增加签名或校验机制防止伪造，也可加密uuid（可逆加密）
        String accessToken = UUID.randomUUID().toString();
//        String accessToken = AesEncipherUtils.decrypt(key);
        redisTemplate.opsForValue().set(RedisHeaders.ACCESS_TOKEN + accessToken, token, JwtUtil.EXPIRE_TIME, TimeUnit.MILLISECONDS);

//        缓存登录信息并更新登录状态
        redisTemplate.opsForValue().set(RedisHeaders.USER_CACHE + authVo.getUsername(), JsonUtils.toJson(authVo), 1000 * 60 * 10, TimeUnit.MILLISECONDS);
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setId(authVo.getId());
        String ip = GetClientIp.getUserIp(request);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(RedisHeaders.REFRESH_TOKEN + authVo.getId()))) {
            String refreshToken = UUID.randomUUID().toString();
            tokenDTO.setRefreshToken(refreshToken);
            redisTemplate.opsForValue().setIfAbsent(
                    RedisHeaders.REFRESH_TOKEN + authVo.getId(),
                    refreshToken, JwtUtil.REFRESH_TOKEN_EXPIRE_TIME,
                    TimeUnit.MILLISECONDS
            );
        }
        tokenDTO.setAccessToken(accessToken);
        tokenDTO.setIp(ip);
        Resp<String> resp = userClient.update(tokenDTO);
        if (resp.getCode().equals(StatusCode.UPDATE_SUCCESS)) {
            redisTemplate.delete(RedisHeaders.ACCESS_TOKEN + resp.getData());
            return Resp.success("ok", StatusCode.SELECT_SUCCESS, accessToken);
        }
        return Resp.error("令牌生成失败", StatusCode.UPDATE_ERR);
    }

//    @GetMapping("/get_user_info_by_uuid/{uuid}")
//    public AuthUserDTO getUserInfoByUuid(@PathVariable String uuid) {
//        String token = redisTemplate.opsForValue().get(uuid);
//        if (StringUtils.isNotBlank(token)) {
//            Map<String, String> userInfoMap = JwtUtil.parseToken(token);
//            if (!userInfoMap.isEmpty()) {
//                AuthUserDTO authUserDTO = new AuthUserDTO();
//                authUserDTO.setId(userInfoMap.get("userid"));
//                authUserDTO.setUsername(userInfoMap.get("username"));
//                authUserDTO.setRoles(userInfoMap.get("roles"));
//                System.out.println("authUserDTO => " + authUserDTO);
//                return authUserDTO;
//            }
//        }
//        return null;
//    }

    @GetMapping("/check_code")
    public void checkCode(@RequestParam String uid, HttpServletResponse response) {
        response.setContentType("image/jpg");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        GifCaptcha gifCaptcha = new GifCaptcha(130, 48, 4);
        gifCaptcha.setCharType(Captcha.TYPE_NUM_AND_UPPER);
        TextEntry text = gifCaptcha.createText();
        log.info(" text => " + text.getKey());
        redisTemplate.opsForValue().set(RedisHeaders.CHECK_CODE_UNIQUE_ID + uid, String.valueOf(text.getKey()), 120, TimeUnit.SECONDS);
        try {
            gifCaptcha.out(response.getOutputStream(), text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

