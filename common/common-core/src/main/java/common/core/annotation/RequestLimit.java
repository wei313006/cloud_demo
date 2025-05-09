package common.core.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

//    令牌桶算法，
//    桶以固定的速率生成令牌（token），并将这些令牌放入桶中。
//    每当有请求到来时，首先要从桶中获取一个令牌，如果桶中没有足够的令牌，则请求将被限流。
//    当桶中的令牌数耗尽时，额外的请求将等待，直到桶中产生新的令牌，使得请求可以获取到令牌继续执行。

    double num() default 1; // 第一次查询后默认次数会 + 1

    String method() default "";
}
