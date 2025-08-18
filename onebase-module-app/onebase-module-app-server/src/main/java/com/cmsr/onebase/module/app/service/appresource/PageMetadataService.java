package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.api.appresource.dto.PageMetadataRespDTO;

@Service
public interface PageMetadataService {

    PageMetadataRespDTO getPageMetadata(Long id);

    Boolean deletePageMetadata(Long id);

}
