package user.service;

import user.entity.UserRoles;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/23 18:13
 */
public interface UserRolesService {

    List<UserRoles> findByUid(long uid);

}
