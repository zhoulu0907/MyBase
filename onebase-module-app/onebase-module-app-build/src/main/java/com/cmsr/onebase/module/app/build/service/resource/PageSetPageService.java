package com.cmsr.onebase.module.app.build.service.resource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetPageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetPageRespDTO;

@Service
public interface PageSetPageService {

    PageSetPageRespDTO getPageSetPage(Long id);

    Long createPageSetPage(CreatePageSetPageDTO createPageSetPageDTO);

    Boolean deletePageSetPage(Long id);

    Boolean updatePageSetPage(PageSetPageRespDTO pageSetPageRespDTO);

    List<PageSetPageRespDTO> getPageSetPageList(Long pageSetId);

}
