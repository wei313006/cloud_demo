package user.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import user.dao.RolePermissionsDao;
import user.entity.RolePermissions;
import user.service.RolePermissionsService;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/23 18:15
 */

@Service
public class RolePermissionsServiceImpl implements RolePermissionsService {

    @Resource
    private RolePermissionsDao rolePermissionsDao;

    @Override
    public List<RolePermissions> findByRoleId(List<Integer> roleList) {
        return rolePermissionsDao.findByRoleIds(roleList);
    }
}
