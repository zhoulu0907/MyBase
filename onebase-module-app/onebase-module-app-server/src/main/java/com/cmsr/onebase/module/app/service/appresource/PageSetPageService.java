package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetPageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetPageRespDTO;

@Service
public interface PageSetPageService {

    PageSetPageRespDTO getPageSetPage(Long id);

    Long createPageSetPage(CreatePageSetPageDTO createPageSetPageDTO);

    Boolean deletePageSetPage(Long id);

    Boolean updatePageSetPage(PageSetPageRespDTO pageSetPageRespDTO);

    List<PageSetPageRespDTO> getPageSetPageList(Long pageSetId);

}
