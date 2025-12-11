package com.cmsr.menu.server;

import com.cmsr.api.menu.MenuApi;
import com.cmsr.api.menu.vo.MenuVO;
import com.cmsr.i18n.I18n;
import com.cmsr.menu.dao.auto.entity.CoreMenu;
import com.cmsr.menu.manage.MenuManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuServer implements MenuApi {

    @Resource
    private MenuManage menuManage;

    @I18n
    @Override
    public List<MenuVO> query() {
        List<CoreMenu> coreMenus = menuManage.coreMenus();
        return menuManage.query(new ArrayList<>(coreMenus));
    }
}
