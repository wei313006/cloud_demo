package user.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.security.filter.BaseSecurityFilter;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import common.security.entity.SecurityHeaders;
import user.entity.RolePermissions;
import user.service.PermissionsService;
import user.service.RolePermissionsService;
import user.service.RolesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author abing
 * @created 2025/4/18 21:52
 */

@Component
@Import(BaseSecurityFilter.class)
public class SecurityFilterConfig  {

//    @Resource
//    private RolesService rolesService;
//
//    @Resource
//    private PermissionsService permissionsService;
//
//    @Resource
//    private RolePermissionsService rolePermissionsService;
//
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        String username = request.getHeader(SecurityHeaders.USERNAME);
//        String userid = request.getHeader(SecurityHeaders.USERID);
//        String roles = request.getHeader(SecurityHeaders.ROLES);
//        log.error(username + " => " + userid + " => " + roles + StringUtils.hasText(username) + StringUtils.hasText(roles));
//        if (StringUtils.hasText(userid) && StringUtils.hasText(username) && StringUtils.hasText(roles)) {
//
////            获取用户角色
//            ObjectMapper objectMapper = new ObjectMapper();
//            List<Integer> rolesList = objectMapper.readValue(roles, objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
////            List<UserRoles> userRolesList = userRolesService.findByUid(Long.parseLong(userid));
//            if (!rolesList.isEmpty()) {
////                int[] roleIds = new int[userRolesList.size()];
////                for (int i = 0; i < userRolesList.size(); i++) {
////                    roleIds[i] = userRolesList.get(i).getRid();
////                }
////            获取用户角色权限
//                List<RolePermissions> rolePermissionsList = rolePermissionsService.findByRoleId(rolesList);
//                if (!rolePermissionsList.isEmpty()) {
//                    List<Integer> permissionList = new ArrayList<>();
//                    for (RolePermissions rolePermissions : rolePermissionsList) {
//                        permissionList.add(rolePermissions.getPermissionId());
//                    }
//
//                    //            配置SecurityContextHolder
//                    Authentication auth =
//                            new UsernamePasswordAuthenticationToken(username, null, getAuthorities(rolesList, permissionList));
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            }
//        }
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    public Collection<? extends GrantedAuthority> getAuthorities(List<Integer> roleList, List<Integer> permissionList) {
//        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        if (roleList.size() > 0) {
//            List<String> rolesList = rolesService.findByIds(roleList);
//            for (String role : rolesList) {
//                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
//            }
//        }
//        if (permissionList.size() > 0) {
//            List<String> permissionsList = permissionsService.findByIds(permissionList);
//            for (String perm : permissionsList) {
//                grantedAuthorities.add(new SimpleGrantedAuthority("PERMISSION_" + perm));
//            }
//        }
//        return grantedAuthorities;
//    }
}
