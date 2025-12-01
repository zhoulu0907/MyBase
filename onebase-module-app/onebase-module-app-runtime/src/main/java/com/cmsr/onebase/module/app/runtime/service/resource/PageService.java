package com.cmsr.onebase.module.app.runtime.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PageService {

    PageRespDTO getPage(Long pageId);

    List<PageDTO> getFormPageListByAppId(Long appId);

    String getMetadataByPageId(String pageUuid);

    List<PageDTO> listPageView(String pageSetUuid);

}
