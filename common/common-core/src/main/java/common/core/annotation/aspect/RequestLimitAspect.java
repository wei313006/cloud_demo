package common.core.annotation.aspect;

import com.google.common.util.concurrent.RateLimiter;
import common.core.exception.CustomRequestAbortedException;
import common.core.annotation.RequestLimit;
import common.core.utils.GetClientIp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class RequestLimitAspect {


    private RateLimiter rateLimiter;

    private final ConcurrentHashMap<String, RateLimiter> limiterMap = new ConcurrentHashMap<>();

    @Autowired
    private HttpServletRequest request;


    @Pointcut("@annotation(common.core.annotation.RequestLimit)")
    public void LimiterPointCut() {

    }

    @Around("LimiterPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取注解对象
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequestLimit annotation = method.getAnnotation(RequestLimit.class);
        double num = annotation.num();
        String reqMethod = annotation.method();
        String requestURI = request.getRequestURI();

        String key = GetClientIp.getUserIp(this.request) + (reqMethod.equals("") ? requestURI : reqMethod);
        log.info("req_key = " + requestURI + " _ " + key);

        if (!limiterMap.containsKey(key)) {
            limiterMap.put(key, RateLimiter.create(num));
        }
        rateLimiter = limiterMap.get(key);

        if (rateLimiter.tryAcquire()) {
            return point.proceed();
        } else {
            throw new CustomRequestAbortedException("You're moving too fast.");
        }

    }
}
