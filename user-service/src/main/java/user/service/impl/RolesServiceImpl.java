package user.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import user.dao.RolesDao;
import user.entity.Roles;
import user.service.RolesService;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/17 15:29
 */

@Service
public class RolesServiceImpl implements RolesService {
    @Resource
    private RolesDao rolesDao;

    public Roles findById(int id) {
        return rolesDao.selectById(id);
    }

    @Override
    public List<String> findByIds(List<Integer> roleList) {
        return rolesDao.findByIds(roleList);
    }
}
