package user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author abing
 * @created 2025/5/21 15:16
 */

@Data
@TableName("menu")
public class Menu {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "label")
    private String label;

    @TableField(value = "icon")
    private String icon;

    @TableField(value = "route_path")
    private String routePath;

    @TableField(value = "parent_id")
    private int parentId;

    @TableField(value = "component")
    private String component;

    @TableField(value = "perm_id")
    private String permId;

    @TableField(value = "perm_code")
    private String permCode;

    @TableField(value = "is_deleted")
    private int isDeleted;

    @TableField(value = "create_by")
    private String createBy;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "update_time")
    private String updateTime;

    @TableField(value = "is_public")
    private int isPublic;

    @TableField(value = "sort")
    private int sort;

}
