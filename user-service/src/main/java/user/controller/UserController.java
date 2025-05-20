package user.controller;

import common.core.annotation.SysLog;
import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.entity.dto.AuthUserDTO;
import common.core.exception.AuthException;
import common.core.entity.dto.TokenDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import common.security.entity.SecurityHeaders;
import user.entity.RolePermissions;
import user.entity.User;
import user.entity.UserRoles;
import user.service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author abing
 * @created 2025/3/27 15:12
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RolesService rolesService;

    @Resource
    private PermissionsService permissionsService;

    @Resource
    private RolePermissionsService rolePermissionsService;

    @Resource
    private UserRolesService userRolesService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @SysLog
    @PostMapping("/auth/login")
    public Resp<AuthUserDTO> login(@RequestBody AuthUserDTO authUserDTO) {
        User user = userService.findByUsername(authUserDTO.getUsername());
        if (Objects.isNull(user)) {
            throw new AuthException("用户名或密码错误！");
        }

//        采用自定义密码比对，不使用authenticationManager对象
        boolean matches = passwordEncoder.matches(authUserDTO.getPassword(), user.getPassword());
        if (matches) {
//        获取用户角色表
            return generateAuthUserDto(user, 0);
        }
        throw new AuthException("用户名或密码错误!");
    }

    @SysLog(explain = "通过token获取用户信息")
    @GetMapping("/access_token/{accessToken}")
    public Resp<AuthUserDTO> findByAccessToken(@PathVariable String accessToken) {
        User user = userService.findByAccessToken(accessToken);
        if (Objects.isNull(user)) {
            return Resp.error("查询不到用户", StatusCode.SELECT_ERR);
        }
        return generateAuthUserDto(user, 0);
    }

    @SysLog(explain = "修改用户信息")
    @PutMapping("/update")
    public Resp<String> update(@RequestBody TokenDTO tokenDTO) {
        String id = tokenDTO.getId();
        if (StringUtils.isBlank(id)) {
            return Resp.error("id字段为空", StatusCode.UPDATE_ERR);
        }
        User user = userService.findById(Long.parseLong(id));
        if (Objects.isNull(user)) {
            return Resp.error("用户不存在", StatusCode.UPDATE_ERR);
        }
        String oldAccessToken = user.getAccessToken();
        user.setAccessToken(tokenDTO.getAccessToken());
        if (StringUtils.isNotBlank(tokenDTO.getRefreshToken())) {
            user.setRefreshToken(tokenDTO.getRefreshToken());
        }
        user.setIp(tokenDTO.getIp());
        return userService.update(user) > 0 ?
                Resp.success("ok", StatusCode.UPDATE_SUCCESS, oldAccessToken)
                : Resp.error("failed", StatusCode.UPDATE_ERR);
    }

    //   security6.0++版本hasPermission方法最少传递两个参数
    @GetMapping("/find_all")
    @PreAuthorize("hasAnyRole('super_admin', 'admin') and hasPermission(null ,'user_view')")
    public Resp<List<User>> findAll(HttpServletRequest request) {
        System.out.println(request.getHeader(SecurityHeaders.USERNAME));
        System.out.println(request.getHeader(SecurityHeaders.USERID));
        System.out.println(request.getHeader(SecurityHeaders.ROLES));
//        System.out.println(SecurityUtils.getUserDetail() + " 1 ");
        System.out.println(SecurityContextHolder.getContext().getAuthentication() + " 2 ");
//        UserDetails userDetail = SecurityUtils.getUserDetail();
        return Resp.success("ok", StatusCode.SELECT_SUCCESS, userService.findAll());
    }

    @GetMapping("/generate/role_perm/{id}")
    public Resp<AuthUserDTO> generateRolePermDto(@PathVariable String id) {
        if (StringUtils.isNotBlank(id)) {
            try {
                User user = userService.findById(Long.parseLong(id));
                if (Objects.isNull(user)) {
                    return Resp.error("failed", StatusCode.SELECT_ERR);
                }
                return generateAuthUserDto(user, 1);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("非法id参数" + e.getMessage());
            }
        }
        throw new NumberFormatException("非法id参数");
    }


    @GetMapping("/find_by_id/{id}")
    public Resp<User> findById(@PathVariable() String id) {
        if (StringUtils.isNotBlank(id)) {
            try {
                return Resp.success("ok", StatusCode.SELECT_SUCCESS, userService.findById(Long.parseLong(id)));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("非法id参数" + e.getMessage());
            }
        }
        throw new NumberFormatException("非法id参数");

    }

    @GetMapping("/admin/{id}")
    public Resp<User> admin(@PathVariable String id) {
        if (StringUtils.isNotBlank(id)) {
            try {
                return Resp.success("ok", StatusCode.SELECT_SUCCESS, userService.findById(Long.parseLong(id)));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("非法参数" + e.getMessage());
            }
        }
        throw new NumberFormatException("非法参数");
    }

    public Resp<AuthUserDTO> generateAuthUserDto(User user, int type) {

//        生成用户登录信息
        AuthUserDTO authVo = new AuthUserDTO();
        authVo.setId(user.getId().toString());
        authVo.setUsername(user.getUsername());
        authVo.setAccessToken(user.getAccessToken());
        authVo.setRefreshToken(user.getRefreshToken());

        if (type == 1) {
            List<UserRoles> userRolesList = userRolesService.findByUid(user.getId());
            if (userRolesList.isEmpty()) {
                throw new AuthException("用户角色为空");
            }
            List<Integer> userRolesIds = userRolesList.stream()
                    .map(UserRoles::getRid)
                    .collect(Collectors.toList());
//        获取角色权限表
            List<RolePermissions> rolePermissionsList = rolePermissionsService.findByRoleId(userRolesIds);
            List<Integer> permissionIds = rolePermissionsList.stream()
                    .map(RolePermissions::getPermissionId)
                    .collect(Collectors.toList());

            List<String> userRoles = rolesService.findByIds(userRolesIds);
            authVo.setRoles(userRoles);
            if (!permissionIds.isEmpty()) {
                List<String> permissions = permissionsService.findByIds(permissionIds);
                authVo.setPermissions(permissions);
            } else {
                authVo.setPermissions(new ArrayList<>());
            }
        }
        return Resp.success("ok", StatusCode.SELECT_SUCCESS, authVo);
    }

}
