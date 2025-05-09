package user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author abing
 * @created 2025/4/23 17:52
 * 用户角色表
 */

@Data
@TableName("user_roles")
public class UserRoles {

    @TableId(value = "uid")
    private Long uid;

    @TableField(value = "rid")
    private int rid;

}
