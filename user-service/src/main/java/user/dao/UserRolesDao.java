package user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import user.entity.UserRoles;

/**
 * @author abing
 * @created 2025/4/23 18:07
 */

@Mapper
public interface UserRolesDao extends BaseMapper<UserRoles> {
}
