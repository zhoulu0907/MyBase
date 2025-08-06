package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.admin.app.vo.*;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
public interface AppMenuService {

    List<MenuListRespVO> listApplicationMenu(Long applicationId);

    MenuCreateRespVO createApplicationMenu(MenuCreateReqVO createReqVO);

    void deleteApplicationMenu(Long id);

    void updateApplicationMenuName(Long id, String menuName);

    void updateApplicationMenuOrder(MenuOrderUpdateReqVO updateReqVO);

    void updateApplicationMenuVisible(Long id, Boolean visible);

    void copyApplicationMenu(MenuCopyReqVO copyReqVO);

}
