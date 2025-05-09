package user.service;

import org.springframework.stereotype.Service;
import user.entity.Roles;

import java.util.List;

/**
 * @author abing
 * @created 2025/4/15 17:11
 */
@Service
public interface RolesService {

    Roles findById(int id);

    List<String> findByIds(List<Integer> roleList);

}
