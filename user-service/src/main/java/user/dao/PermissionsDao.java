package user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import user.entity.Permissions;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/23 18:08
 */

@Mapper
public interface PermissionsDao extends BaseMapper<Permissions> {
    @Select("""
            <script>
                select name from permissions where id in 
                <foreach collection='permissionsIds' item='item' open="(" separator="," close=")">
                        #{item}
                </foreach>
             </script>
            """)
    List<String> findByIds(List<Integer> permissionsIds);
}
