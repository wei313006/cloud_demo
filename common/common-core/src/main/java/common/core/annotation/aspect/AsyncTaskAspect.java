package common.core.annotation.aspect;

import common.core.annotation.AsyncTask;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author abing
 * @created 2025/3/12 11:19
 */

@Aspect
@Component
public class AsyncTaskAspect {

    @Autowired
    private Map<String, Executor> asyncExecutors;

    @Around("@annotation(asyncTask)")
    public Object around(ProceedingJoinPoint joinPoint, AsyncTask asyncTask) throws Throwable {
        String poolName = asyncTask.pool();
        Executor executor = asyncExecutors.get(poolName);

        if (executor == null) {
            throw new IllegalArgumentException("线程池 " + poolName + " 不存在！");
        }

//        手动提交任务到指定线程池
        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, executor);
//        如果需要阻塞等待结果，否则返回 null
        return future.get();
    }
}
