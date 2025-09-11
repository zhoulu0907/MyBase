package com.cmsr.onebase.module.app.build.service.appresource;

import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageRespDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.UpdatePageNameDTO;

import java.util.List;

@Service
public interface PageService {

    PageRespDTO getPage(Long pageId);

    Long createPage(CreatePageDTO createPageDTO);

    Boolean updatePageName(UpdatePageNameDTO updatePageNameVO);

    Boolean deletePage(Long pageId);

    List<PageDTO> getFormPageListByAppId(Long appId);

}
