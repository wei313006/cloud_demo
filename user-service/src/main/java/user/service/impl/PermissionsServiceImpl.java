package user.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import user.dao.PermissionsDao;
import user.entity.Permissions;
import user.service.PermissionsService;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/23 18:14
 */

@Service
public class PermissionsServiceImpl implements PermissionsService {

    @Resource
    private PermissionsDao permissionsDao;

    @Override
    public List<String> findByIds(List<Integer> permissionIds) {
        return permissionsDao.findByIds(permissionIds);
    }
}
