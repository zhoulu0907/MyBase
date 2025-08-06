package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageMetadataDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageMetadataRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.UpdatePageMetadataDTO;
import com.cmsr.onebase.module.app.dal.database.appresource.AppPageMetadataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageMetadataDO;

import jakarta.annotation.Resource;

@Service
public class PageMetadataServiceImpl implements PageMetadataService {

    @Resource
    private AppPageMetadataRepository dataRepository;

    @Override
    public PageMetadataRespDTO getPageMetadata(Long id) {
        PageMetadataDO pageMetadataDO = dataRepository.findById(id);
        return BeanUtils.toBean(pageMetadataDO, PageMetadataRespDTO.class);
    }

    @Override
    public Long createPageMetadata(CreatePageMetadataDTO createPageMetadataDTO) {
        PageMetadataDO pageMetadataDO = BeanUtils.toBean(createPageMetadataDTO, PageMetadataDO.class);
        pageMetadataDO = dataRepository.insert(pageMetadataDO);
        return pageMetadataDO.getId();
    }

    @Override
    public Boolean deletePageMetadata(Long id) {
        dataRepository.deleteById(id);
        return true;
    }

    @Override
    public Boolean updatePageMetadata(UpdatePageMetadataDTO updatePageMetadataDTO) {
        PageMetadataDO pageMetadataDO = BeanUtils.toBean(updatePageMetadataDTO, PageMetadataDO.class);
        pageMetadataDO = dataRepository.update(pageMetadataDO);
        return true;
    }
}
