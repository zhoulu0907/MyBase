package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PageService {

    PageRespDTO getPage(Long pageId);

    Long createPage(CreatePageDTO createPageDTO);

    Long createPageView(CreatePageViewDTO createPageViewDTO);

    Boolean updatePageName(UpdatePageNameDTO updatePageNameVO);

    List<PageDTO> getFormPageListByAppId(Long appId);

    String getMetadataByPageUuid(String pageUuid);

    List<PageDTO> listPageView(String pageSetUuid);

}
