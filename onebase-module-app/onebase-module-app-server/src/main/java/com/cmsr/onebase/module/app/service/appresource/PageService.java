package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;

@Service
public interface PageService {

    PageRespDTO getPage(String pageCode);

    Long createPage(CreatePageDTO createPageDTO);

    Boolean deletePage(String code);
}
