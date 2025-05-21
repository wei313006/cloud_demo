package user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import user.entity.Menu;

/**
 * @author abing
 * @created 2025/5/21 15:29
 */

@Mapper
public interface MenuDao extends BaseMapper<Menu> {
}
