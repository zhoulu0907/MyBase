package com.cmsr.onebase.module.app.build.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageRefRouterDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageRefRouterRespDTO;

@Service
public interface PageRefRouterService {

    PageRefRouterRespDTO getPageRefRouter(Long id);

    Long createPageRefRouter(CreatePageRefRouterDTO createPageRefRouterDTO);

    Boolean deletePageRefRouter(Long id);

    Boolean updatePageRefRouter(PageRefRouterRespDTO pageRefRouterRespDTO);

    List<PageRefRouterRespDTO> getPageRefRouterList(Long pageId);

}
