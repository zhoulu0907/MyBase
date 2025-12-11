package com.cmsr.onebase.module.app.build.service.menu;

import com.cmsr.onebase.module.app.build.vo.menu.*;
import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
public interface AppMenuService {

    List<MenuListRespVO> listBpmApplicationMenu(Long applicationId);

    void createDefaultBpmMenu(Long applicationId);

    List<MenuListRespVO> listApplicationMenu(Long applicationId, String name);

    MenuCreateRespVO createApplicationMenu(MenuCreateReqVO createReqVO);

    void deleteApplicationMenu(Long id);

    void updateApplicationMenuName(Long id, String menuName);

    void updateApplicationMenu(MenuUpdateReqVO updateReqVO);

    void updateApplicationMenuOrder(MenuOrderUpdateReqVO updateReqVO);

    void updateApplicationMenuVisible(Long id, Integer visible);

    MenuCreateRespVO copyApplicationMenu(MenuCopyReqVO copyReqVO);
}
