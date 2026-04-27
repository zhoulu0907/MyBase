package com.cmsr.onebase.module.app.build.service.resource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;

@Service
public interface ComponentService {

    List<ComponentDTO> listComponentByPageId(Long pageId);

    List<ComponentDTO> listComponentByPageUuid(String pageUuid);

    /**
     * 根据应用ID查询当前应用下 page_type 为 list 的所有页面对应的组件并返回
     *
     * @param applicationId 应用ID
     * @return 所有 list 类型页面下的组件列表
     */
    List<ComponentDTO> listComponentForListPages(Long applicationId);

}
