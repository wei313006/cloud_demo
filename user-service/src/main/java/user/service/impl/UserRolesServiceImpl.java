package user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import user.dao.UserRolesDao;
import user.entity.UserRoles;
import user.service.UserRolesService;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/23 18:15
 */

@Service
public class UserRolesServiceImpl implements UserRolesService {

    @Resource
    private UserRolesDao userRolesDao;

    @Override
    public List<UserRoles> findByUid(long uid) {
        QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return userRolesDao.selectList(queryWrapper);
    }
}
