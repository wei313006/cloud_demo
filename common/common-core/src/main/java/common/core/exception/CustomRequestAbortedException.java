package common.core.exception;

public class CustomRequestAbortedException extends RuntimeException {

    private Integer code;

    public CustomRequestAbortedException(String msg) {
        super(msg);
    }

    public CustomRequestAbortedException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public CustomRequestAbortedException(String message, Throwable cause, Integer code) {
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
