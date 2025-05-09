package user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author abing
 * @created 2025/4/23 18:00
 * 角色权限表
 */

@Data
@TableName("role_permissions")
public class RolePermissions {

    @TableId(value = "role_id")
    private int roleId;

    @TableField(value = "permission_id")
    private int permissionId;

}
