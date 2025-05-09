package common.core.annotation;

import java.lang.annotation.*;

/**
 * @author abing
 * @created 2025/3/12 10:58
 * 自定义缓存注解
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomCache {
    String key();         // 缓存键
    int ttl() default 30; // 缓存时间（秒）
    boolean cacheIf() default true; // 是否满足条件才缓存
}
