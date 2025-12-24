package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dto.resource.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PageService {

    Long createPageView(CreatePageViewDTO createPageViewDTO);

    Boolean updatePageName(UpdatePageNameDTO updatePageNameVO);

    List<PageDTO> getPageListByAppId(Long appId);

    String getMetadataByPageId(Long pageId);

    String getMetadataByPageUuid(String pageUuid);

    List<PageDTO> listPageView(Long pageSetId);

}
