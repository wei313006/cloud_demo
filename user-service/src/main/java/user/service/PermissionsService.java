package user.service;

import user.entity.Permissions;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/23 18:13
 */
public interface PermissionsService {

    List<String> findByIds(List<Integer> permissionIds);
}
