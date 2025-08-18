package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.PageMetadataRespDTO;
import com.cmsr.onebase.module.app.dal.database.appresource.AppPageMetaRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageMetadataDO;

import jakarta.annotation.Resource;

@Service
public class PageMetadataServiceImpl implements PageMetadataService {

    @Resource
    private AppPageMetaRepository appPageMetaDataRepository;

    @Override
    public PageMetadataRespDTO getPageMetadata(Long id) {
        PageMetadataDO pageMetadataDO = appPageMetaDataRepository.findById(id);
        return BeanUtils.toBean(pageMetadataDO, PageMetadataRespDTO.class);
    }

    @Override
    public Boolean deletePageMetadata(Long id) {
        appPageMetaDataRepository.deleteById(id);
        return true;
    }

}
