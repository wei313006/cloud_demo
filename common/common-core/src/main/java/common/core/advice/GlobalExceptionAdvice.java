package common.core.advice;

import common.core.entity.Resp;
import common.core.entity.StatusCode;
import common.core.exception.AuthException;
import common.core.exception.BusinessException;
import common.core.exception.CustomRequestAbortedException;
import common.core.exception.SystemException;
//import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author abing
 * @created 2025/4/21 16:24
 * 异常拦截处理
 */

@RestControllerAdvice
public class GlobalExceptionAdvice {

//    @ExceptionHandler(TransactionSystemException.class)
//    public Resp doTransactionSystemException(TransactionSystemException ex) {
//        return Resp.error(ex.getMessage(), StatusCode.UN_AUTHORIZED_EXCEPTION);
//    }

    @ExceptionHandler(AuthException.class)
    public Resp<String> doAuthException(AuthException ex) {
        return Resp.error(ex.getMessage(), StatusCode.AUTHORIZED_EXCEPTION);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Resp<String> doBadCredentialsException(BadCredentialsException ex) {
        return Resp.error(ex.getMessage(), StatusCode.AUTHORIZED_EXCEPTION);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public Resp<String> doUsernameNotFoundException(UsernameNotFoundException ex) {
        return Resp.error(ex.getMessage(), StatusCode.AUTHORIZED_EXCEPTION);
    }

//    @ExceptionHandler(ExpiredJwtException.class)
//    public Resp doExpiredJwtException(ExpiredJwtException ex) {
//        return Resp.error(ex.getMessage(), StatusCode.TOKEN_EXPIRE_EXCEPTION);
//    }

    @ExceptionHandler(SystemException.class)
    public Resp<String> doSystemException(SystemException ex) {
        return Resp.error(ex.getMessage(), StatusCode.SYSTEM_EXCEPTION);
    }

    @ExceptionHandler(BusinessException.class)
    public Resp<String> doBusinessException(BusinessException ex) {
        return Resp.error(ex.getMessage(), StatusCode.BUSINESS_EXCEPTION);
    }

    @ExceptionHandler(Exception.class)
    public Resp<String> doOtherException(Exception ex) {
        return Resp.error(ex.getMessage(), StatusCode.UNKNOWN_EXCEPTION);
    }

    @ExceptionHandler(NullPointerException.class)
    public Resp<String> doNullPointerException(NullPointerException ex) {
        return Resp.error(ex.getMessage(), StatusCode.NULL_POINTER_EXCEPTION);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Resp<String> doSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        return Resp.error("存在约束，不能操作", StatusCode.SQL_FOREIGN_EXCEPTION);
    }

    @ExceptionHandler
    public Resp<String> LinkException(ConnectException ex) {
        return Resp.error(ex.getMessage(), StatusCode.CONNECT_EXCEPTION);
    }

    @ExceptionHandler
    public Resp<String> RuntimeException(RuntimeException ex) {
        return Resp.error(ex.getMessage(), StatusCode.RUNTIME_EXCEPTION);
    }

    @ExceptionHandler
    public Resp<String> doCustomRequestAbortedException(CustomRequestAbortedException ex) {
        return Resp.error(ex.getMessage(), StatusCode.REQUEST_LIMIT_EXCEPTION);
    }
}
