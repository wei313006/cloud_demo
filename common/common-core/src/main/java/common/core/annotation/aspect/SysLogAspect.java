package common.core.annotation.aspect;

import common.core.entity.Resp;
import common.core.annotation.SysLog;
import common.core.utils.GetClientIp;
import common.core.utils.TimeFormatUtils;
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
import java.util.Arrays;

@Aspect
@Component
@Slf4j
//@EnableAspectJAutoProxy(proxyTargetClass = false)
public class SysLogAspect {

    @Autowired
    public HttpServletRequest request;

//    @Resource
//    private OperationLogService operationLogService;

    @Pointcut("@annotation(common.core.annotation.SysLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Resp resp = (Resp) joinPoint.proceed();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = method.getAnnotation(SysLog.class);
        String explain = sysLog.explain();
        long currentTime = System.currentTimeMillis();
        log.info("****************SystemLog-start********************");
        log.info("[注解备注] :" + explain);
        log.info("[操作时间] :" + TimeFormatUtils.formatCurrentDateTime());
        log.info("[请求方法] :" + methodName);
        log.info("[请求参数] :" + Arrays.toString(joinPoint.getArgs()));
        log.info("[请求返回值] :" + resp);
        log.info("[请求耗时ms] :" + (System.currentTimeMillis() - currentTime));
        log.info("[请求URL] :" + this.request.getRequestURI());
        log.info("[请求IP] :" + GetClientIp.getUserIp(this.request));
        log.info("****************SystemLog-over********************");
//        if (sysLog.isRecordToDb()) {
//            UserDetails user = GetAuthenticatedUser.getUser();
//            if (Objects.nonNull(user)) {
//                OperationLog operationLog = new OperationLog();
//                operationLog.setContent("管理员 ：" + user.getUsername() + " " + explain + " 响应结果 ：" + resp.getCode());
//                operationLog.setTime(TimeFormatUtils.formatCurrentDateTime());
//                operationLogService.insert(operationLog);
//            }
//        }
        return resp;
    }
}
