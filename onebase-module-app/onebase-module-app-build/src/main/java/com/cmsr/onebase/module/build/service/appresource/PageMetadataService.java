package com.cmsr.onebase.module.build.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.core.dto.appresource.PageMetadataRespDTO;

@Service
public interface PageMetadataService {

    PageMetadataRespDTO getPageMetadata(Long id);

    Boolean deletePageMetadata(Long id);

}
