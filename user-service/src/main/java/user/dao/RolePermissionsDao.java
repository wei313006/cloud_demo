package user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import user.entity.RolePermissions;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/23 18:11
 */

@Mapper
public interface RolePermissionsDao extends BaseMapper<RolePermissions> {

    @Select("""
            <script>
                select * from role_permissions where role_id in 
                <foreach collection='roleList' item='item' open="(" separator="," close=")">
                        #{item}
                </foreach>
             </script>
            """)
    List<RolePermissions> findByRoleIds(List<Integer> roleList);

}
