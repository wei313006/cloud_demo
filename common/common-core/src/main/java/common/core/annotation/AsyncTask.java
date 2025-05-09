package common.core.annotation;

import java.lang.annotation.*;

/**
 * @author abing
 * @created 2025/3/12 11:17
 * 线程池异步任务注解
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncTask {

    // 默认使用 defaultPool
    String pool() default "defaultPool";
}
