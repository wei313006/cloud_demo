package user.controller;

import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import user.service.MenuService;

/**
 * @author abing
 * @created 2025/6/2 17:46
 */

@RestController
@RequestMapping("/user")
public class MenuController {

    @Resource
    private MenuService menuService;

    @PreAuthorize("hasAnyRole('super_admin', 'admin') and hasPermission(null ,'user_view')")
    @GetMapping("/admin/menu/get_menu_data")
    public void getMenuData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        System.out.println(authentication);
    }
}
