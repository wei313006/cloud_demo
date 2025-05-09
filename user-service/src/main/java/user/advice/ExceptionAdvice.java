package user.advice;

import common.core.advice.GlobalExceptionAdvice;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author abing
 * @created 2025/4/21 17:24
 */

@Configuration
@Import(GlobalExceptionAdvice.class)
public class ExceptionAdvice {
}
