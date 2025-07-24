package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionListRespVO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
@Service
@Validated
public class ApplicationMenuServiceImpl implements ApplicationMenuService {


    public List<ApplicationVersionListRespVO> listApplicationMenu(Long applicationId) {
        return null;
    }

    public Long createApplicationMenu(ApplicationMenuCreateReqVO applicationMenuCreateReqVO) {
        return null;
    }

    public void deleteApplicationMenu(Long applicationId, String menuUuid) {
    }

    public void updateApplicationMenuName(Long applicationId, String menuUuid, String menuName) {

    }
}
