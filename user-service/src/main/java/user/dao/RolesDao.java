package user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import user.entity.Roles;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/15 17:10
 */

@Mapper
public interface RolesDao extends BaseMapper<Roles> {

    @Select("""
            <script>
                select `role` from roles where id in 
                <foreach collection='roleList' item='item' open="(" separator="," close=")">
                        #{item}
                </foreach>
             </script>
            """)
    List<String> findByIds(List<Integer> roleList);

}