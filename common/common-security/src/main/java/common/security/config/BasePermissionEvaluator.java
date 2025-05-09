package common.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author abing
 * @created 2025/4/24 11:25
 * 自定义权限认证器
 */

public class BasePermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        if (authentication == null || permission == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String perm = "PERMISSION_" + permission;

        System.out.println(perm + " => " + authentication);

        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase(perm));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false; // 可以不用实现
    }
}
