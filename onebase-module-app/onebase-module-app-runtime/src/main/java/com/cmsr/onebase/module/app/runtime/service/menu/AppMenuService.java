package com.cmsr.onebase.module.app.runtime.service.menu;

import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuPermissionVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
public interface AppMenuService {
    List<MenuListRespVO> listBpmApplicationMenu();

    List<MenuListRespVO> listApplicationMenu( );

    MenuPermissionVO getMenuPermission(Long menuId);
}
