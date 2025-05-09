package common.core.exception;
public class SystemException extends RuntimeException {
    private Integer code;
    public SystemException() {
    }
    public SystemException(Integer code) {
        this.code = code;
    }
    public SystemException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    public SystemException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
}
