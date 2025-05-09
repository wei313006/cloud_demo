package user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author abing
 * @created 2025/4/15 17:08
 * 角色表
 */

@Data
@TableName("role")
//@Schema(description = "角色实体对象")
public class Roles {

//    @Schema(description = "角色id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

//    @Schema(description = "角色权限字段")
    @TableField(value = "role")
    private String role;

//    @Schema(description = "角色描述")
    @TableField(value = "description")
    private String description;

}
