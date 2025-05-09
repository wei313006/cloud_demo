package user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import user.dao.UserDao;
import user.entity.User;
import user.service.UserService;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/17 15:22
 */

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public List<User> findAll() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return userDao.selectList(queryWrapper);
    }

    @Override
    public User findById(long id) {
        return userDao.selectById(id);
    }

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userDao.selectOne(queryWrapper);
    }

    @Override
    public User findByAccessToken(String accessToken) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("access_token", accessToken);
        return userDao.selectOne(queryWrapper);
    }

    @Override
    public int update(User user) {
        return userDao.updateById(user);
    }


}
