package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageRefRouterDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRefRouterRespDTO;

@Service
public interface PageRefRouterService {

    PageRefRouterRespDTO getPageRefRouter(Long id);

    Long createPageRefRouter(CreatePageRefRouterDTO createPageRefRouterDTO);

    Boolean deletePageRefRouter(Long id);

    Boolean updatePageRefRouter(PageRefRouterRespDTO pageRefRouterRespDTO);

    List<PageRefRouterRespDTO> getPageRefRouterList(String pageCode);

}
