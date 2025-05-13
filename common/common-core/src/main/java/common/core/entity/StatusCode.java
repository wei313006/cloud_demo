package common.core.entity;


public class StatusCode {

    public static final Integer SAVE_SUCCESS = 10011;  //保存成功
    public static final Integer DELETE_SUCCESS = 10021;  //删除成功
    public static final Integer UPDATE_SUCCESS = 10031;  //更改成功
    public static final Integer SELECT_SUCCESS = 10041;  //查询成功
    public static final Integer SAVE_ERR = 10010;  //保存失败
    public static final Integer DELETE_ERR = 10020;  //删除失败
    public static final Integer UPDATE_ERR = 10030;  //更改失败
    public static final Integer SELECT_ERR = 10040;  //查询失败
    public static final Integer RUNTIME_EXCEPTION = 20000; // 运行时异常
    public static final Integer SYSTEM_EXCEPTION = 20010; // 系统异常
    public static final Integer BUSINESS_EXCEPTION = 20020; // 业务异常
    public static final Integer UNKNOWN_EXCEPTION = 20030; // 未知异常
    public static final Integer SQL_FOREIGN_EXCEPTION = 20040; // sql约束异常
    public static final Integer NULL_POINTER_EXCEPTION = 20050; // 空指针异常
    public static final Integer CONNECT_EXCEPTION = 20060; // 连接异常
    public static final Integer TOKEN_EXCEPTION = 20070; // token过期
    public static final Integer INTERCEPTOR_ERROR = 20080; // 无权限
    public static final Integer AUTHORIZED_EXCEPTION = 20090; // 未认证
    public static final Integer CHECKCODE_EXCEPTION = 20100; // 验证码错误
    public static final Integer REQUEST_LIMIT_EXCEPTION = 20110; // 请求限流
    public static final Integer TRANSACTION_EXCEPTION = 20200; // 事务异常
    public static final Integer BALANCE_EXCEPTION = 20300; // 余额不足异常

}
