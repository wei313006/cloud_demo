package common.core.annotation.aspect;

import common.config.CommonConfig;
import common.core.entity.Resp;
import common.core.utils.AesEncipherUtils;
import common.core.utils.JsonUtils;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * @author abing
 * @created 2025/3/12 11:36
 * 响应结果加密工具类
 */

@Aspect
@Component
public class RespEncryptAspect {

    @Resource
    private CommonConfig commonConfig;

    @Around("@annotation(common.core.annotation.RespEncrypt)")
    public Object encryptResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed(); // 获取方法返回值
        if (result instanceof Resp<?>) {
            Resp<Object> resp = (Resp<Object>) result;
            Object data = resp.getData();
            if (data != null) {
                String json = JsonUtils.toJson(data);
                String encrypt = AesEncipherUtils.encrypt(
                        commonConfig.getAesKey(),
                        commonConfig.getIvKey(),
                        commonConfig.getPattern(),
                        json);
                resp.setData(encrypt);
            }
            return resp;
        }

        // 非resp类型直接返回
        return result;
    }
}


