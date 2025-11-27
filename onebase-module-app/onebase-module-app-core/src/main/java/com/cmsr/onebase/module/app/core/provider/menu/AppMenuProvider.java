package com.cmsr.onebase.module.app.core.provider.menu;

import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：huangjie
 * @Date：2025/10/25 13:12
 */
@Setter
@Service
public class AppMenuProvider {

    @Autowired
    private AppMenuRepository appMenuRepository;

    public AppMenuDO findByMenuId(Long menuId) {
        return appMenuRepository.getById(menuId);
    }

}
