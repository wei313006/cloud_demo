package user.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import user.dao.MenuDao;
import user.service.MenuService;

/**
 * @author abing
 * @created 2025/5/21 15:30
 */

@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuDao menuDao;

}
