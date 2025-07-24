package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionListRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
public interface ApplicationMenuService {

    public List<ApplicationVersionListRespVO> listApplicationMenu(Long applicationId);

    public Long createApplicationMenu(ApplicationMenuCreateReqVO applicationMenuCreateReqVO);

    public void deleteApplicationMenu(Long applicationId, String menuUuid);

    public void updateApplicationMenuName(Long applicationId, String menuUuid, String menuName);
}
