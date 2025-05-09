package user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import user.dao.UserDao;
import user.entity.User;

import java.util.List;

/**
 * @author abing
 * @created 2025/3/27 15:12
 */

@Service
public interface UserService {

    List<User> findAll();

    User findById(long id);

    User findByUsername(String username);

    int update(User user);

    User findByAccessToken(String accessToken);

}
