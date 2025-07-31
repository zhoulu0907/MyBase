package com.cmsr.onebase.module.app.service.app;

import java.util.List;

import com.cmsr.onebase.module.app.controller.app.vo.ApplicationTagListRespVO;

/**
 * @Author：mickey
 * @Date：2025/7/23 13:40
 */
public interface ApplicationTagService {

    List<ApplicationTagListRespVO> listApplicationTags(Long tenantId, String query);
}
