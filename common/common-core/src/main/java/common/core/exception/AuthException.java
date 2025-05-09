package common.core.exception;

/**
 * @author abing
 * @created 2025/4/30 14:57
 */
public class AuthException extends RuntimeException {
    private Integer code;

    public AuthException() {
    }

    public AuthException(Integer code) {
        this.code = code;
    }

    public AuthException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public AuthException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }

    public AuthException(String message) {
        super(message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}