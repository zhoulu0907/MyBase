package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationMenuCopyReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationMenuGroupCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationMenuListRespVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationMenuOrderUpdateReqVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
public interface ApplicationMenuService {

    List<ApplicationMenuListRespVO> listApplicationMenu(Long applicationId);

    Long createApplicationMenuGroup(ApplicationMenuGroupCreateReqVO createReqVO);

    void deleteApplicationMenu(Long id);

    void updateApplicationMenuName(Long id, String menuName);

    void updateApplicationMenuOrder(ApplicationMenuOrderUpdateReqVO updateReqVO);

    void updateApplicationMenuVisible(Long id, Boolean visible);

    void copyApplicationMenu(ApplicationMenuCopyReqVO copyReqVO);

}
