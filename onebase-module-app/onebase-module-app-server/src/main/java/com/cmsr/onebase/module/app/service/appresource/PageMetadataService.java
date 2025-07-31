package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageMetadataDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageMetadataRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.UpdatePageMetadataDTO;

@Service
public interface PageMetadataService {

    PageMetadataRespDTO getPageMetadata(Long id);

    Long createPageMetadata(CreatePageMetadataDTO createPageMetadataDTO);

    Boolean deletePageMetadata(Long id);

    Boolean updatePageMetadata(UpdatePageMetadataDTO updatePageMetadataDTO);
}
