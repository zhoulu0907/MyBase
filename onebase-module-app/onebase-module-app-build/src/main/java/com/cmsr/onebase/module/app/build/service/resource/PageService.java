package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PageService {

    Long createPageView(CreatePageViewDTO createPageViewDTO);

    Boolean updatePageName(UpdatePageNameDTO updatePageNameVO);

    List<PageDTO> getFormPageListByAppId(Long appId);

    String getMetadataByPageId(Long pageId);

    List<PageDTO> listPageView(Long pageSetId);

}
