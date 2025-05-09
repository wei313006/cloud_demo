package user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import user.entity.User;

/**
 * @author abing
 * @created 2025/3/27 15:11
 */

@Mapper
public interface UserDao extends BaseMapper<User> {

}
