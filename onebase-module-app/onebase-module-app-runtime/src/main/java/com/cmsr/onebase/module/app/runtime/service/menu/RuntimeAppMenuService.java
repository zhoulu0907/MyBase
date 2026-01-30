package com.cmsr.onebase.module.app.runtime.service.menu;

import java.util.List;

import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuPermissionVO;

/**
 * @Author：huangjie
 *                  @Date：2025/7/23 13:40
 */
public interface RuntimeAppMenuService {

    List<MenuListRespVO> listBpmApplicationMenu();

    List<MenuListRespVO> listApplicationMenu(Boolean isDev);

    MenuPermissionVO getMenuPermission(Long menuId);
}
