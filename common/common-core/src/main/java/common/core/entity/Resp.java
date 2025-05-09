package common.core.entity;

import lombok.Data;

@Data
public class Resp<T> {
    private String msg;
    private Integer code;
    private T data;

    public static <T> Resp<T> success(String message, int code, T data) {
        Resp<T> resp = new Resp<>();
        resp.setMsg(message);
        resp.setCode(code);
        resp.setData(data);
        return resp;
    }

    public static <T> Resp<T> error(String message, int code) {
        Resp<T> resp = new Resp<>();
        resp.setMsg(message);
        resp.setCode(code);
        resp.setData(null);
        return resp;
    }
}
