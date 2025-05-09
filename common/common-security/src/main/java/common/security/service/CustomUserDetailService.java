//package security.service;
//
//import jakarta.annotation.Resource;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import security.clients.UserClient;
//import security.entity.AuthUserDTO;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Objects;
//
///**
// * @author abing
// * @created 2025/3/13 15:10
// */
//
////@Service
//public class CustomUserDetailService implements UserDetailsService {
//
//    @Resource
//    private UserClient userClient;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        AuthUserDTO authUserDTO = userClient.loadManager(username);
//        if (Objects.nonNull(authUserDTO)) {
//            return createManagerDetails(authUserDTO);
//        }
//        throw new UsernameNotFoundException("User not found : " + username);
//    }
//
//    private UserDetails createManagerDetails(AuthUserDTO authUserDTO) {
//        return new org.springframework.security.core.userdetails.User(
//                authUserDTO.getUsername(),
//                authUserDTO.getPassword(),
//                getAuthorities(authUserDTO.getRole()));
//    }
//
//    public Collection<? extends GrantedAuthority> getAuthorities(String role) {
//        if (role != null && role.length() > 0) {
//            Collection<GrantedAuthority> roleList = new ArrayList<>();
//            roleList.add(new SimpleGrantedAuthority("ROLE_" + role));
//            return roleList;
//        }
//        return null;
//    }
//
//
//}
