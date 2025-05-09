package common.core.annotation.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.core.entity.Resp;
import common.core.utils.AesEncipherUtils;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author abing
 * @created 2025/3/12 11:36
 */

@Aspect
@Component
public class RespEncryptAspect {

    @Resource
    private ObjectMapper objectMapper;

    @Around("@annotation(common.core.annotation.RespEncrypt)")
    public Object encryptResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Resp resp = (Resp) joinPoint.proceed(); // 获取方法返回值
        if (resp == null) {
            return null;
        }
        // 将返回数据转换为json字符串
        Object data = resp.getData();
        String json = objectMapper.writeValueAsString(data);
        String encrypt = AesEncipherUtils.encrypt(json);
        resp.setData(encrypt);
        // 返回加密后的数据
        return resp;
    }
}

