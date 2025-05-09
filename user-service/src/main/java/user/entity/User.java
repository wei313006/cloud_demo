package user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author abing
 * @created 2025/3/27 15:10
 * 用户表
 */

@Data
@TableName("user")
public class User {

    @TableId(value = "id")
    private Long id;

    @TableField(value = "username")
    private String username;

    @TableField(value = "email")
    private String email;

    @TableField(value = "password")
    private String password;

    @TableField(value = "sort")
    private int sort;

    @TableField(value = "logic_delete")
    private String logicDelete;

    @TableField(value = "access_token")
    private String accessToken;

    @TableField(value = "refresh_token")
    private String refreshToken;

    @TableField(value = "status")
    private String status;

    @TableField(value = "area")
    private String area;

    @TableField(value = "ip")
    private String ip;

    @TableField(value = "register_time")
    private String registerTime;

    @TableField(value = "avatar")
    private String avatar;

    @TableField(exist = false)
    private String checkCode;

    @TableField(exist = false)
    private String checkCodeId;

}
