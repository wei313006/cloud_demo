package common.core.annotation;

import java.lang.annotation.*;

/**
 * @author abing
 * @created 2025/3/12 11:08
 * 响应结果data加密
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RespEncrypt {

}
