package user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author abing
 * @created 2025/4/23 18:04
 * 操作资源表
 */

@Data
@TableName("resources")
public class Resources {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "description")
    private String description;

    @TableField(value = "resource_type")
    private String resourceType;

    @TableField(value = "resource_url")
    private String resourceUrl;

    @TableField(value = "parent_id")
    private String parentId;

    @TableField(value = "created_by")
    private String createdBy;

    @TableField(value = "updated_at")
    private String updatedAt;

}
