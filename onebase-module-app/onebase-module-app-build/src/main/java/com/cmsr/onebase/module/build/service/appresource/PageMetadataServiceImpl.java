package com.cmsr.onebase.module.build.service.appresource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.appresource.AppPageMetaRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageMetadataDO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageMetadataRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
