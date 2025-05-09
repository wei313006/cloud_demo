package common.core.exception;

import java.io.IOException;

public class IoException extends IOException {
    private Integer code;
    public IoException() {
    }
    public IoException(Integer code) {
        this.code = code;
    }
    public IoException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    public IoException(String message, Throwable cause, Integer code) {
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
