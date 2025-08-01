package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageComponentDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageComponentRespDTO;

@Service
public interface PageComponentService {

    PageComponentRespDTO getPageComponent(Long id);

    Long createPageComponent(CreatePageComponentDTO createPageComponentDTO);

    Boolean deletePageComponent(Long id);

    Boolean updatePageComponent(PageComponentRespDTO pageComponentRespDTO);

    List<PageComponentRespDTO> getPageComponentList(Long pageId);

}
