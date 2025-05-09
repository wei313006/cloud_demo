package common.core.exception;
public class NullPointerException extends RuntimeException {
    private Integer code;
    public NullPointerException() {
    }
    public NullPointerException(Integer code) {
        this.code = code;
    }
    public NullPointerException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    public NullPointerException(String message, Throwable cause, Integer code) {
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
