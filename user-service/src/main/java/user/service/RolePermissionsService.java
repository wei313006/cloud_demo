package user.service;

import user.entity.RolePermissions;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/23 18:13
 */
public interface RolePermissionsService {

    List<RolePermissions> findByRoleId(List<Integer> roleIds);

}
