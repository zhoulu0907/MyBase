package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuListRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
public interface ApplicationMenuService {

    List<ApplicationMenuListRespVO> listApplicationMenu(Long applicationId);

    Long createApplicationMenu(ApplicationMenuCreateReqVO applicationMenuCreateReqVO);

    void deleteApplicationMenu(Long applicationId, String menuUuid);

    void updateApplicationMenuName(Long applicationId, String menuUuid, String menuName);
}
