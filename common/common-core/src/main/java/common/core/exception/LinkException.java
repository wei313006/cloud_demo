package common.core.exception;

import java.net.ConnectException;

public class LinkException extends ConnectException {
    private Integer code;
    public LinkException() {
    }
    public LinkException(Integer code) {
        this.code = code;
    }
    public LinkException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    public LinkException(String message, Throwable cause, Integer code) {
        super(message);
        this.code = code;
    }
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
}
