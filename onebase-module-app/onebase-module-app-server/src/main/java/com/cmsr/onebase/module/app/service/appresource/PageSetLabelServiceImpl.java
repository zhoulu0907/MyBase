package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelCreateDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelUpdateDTO;
import com.cmsr.onebase.module.app.dal.database.appresource.AppPageSetLabelRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetLabelDO;

import jakarta.annotation.Resource;

@Service
public class PageSetLabelServiceImpl implements PageSetLabelService {

    @Resource
    private AppPageSetLabelRepository appPageSetLabelDataRepository;

    @Override
    public List<PageSetLabelRespDTO> getLabelsByPageSetCode(String pagesetCode) {
        List<PageSetLabelDO> pageSetLabelDOs = appPageSetLabelDataRepository.findByPageSetCode(pagesetCode);
        return BeanUtils.toBean(pageSetLabelDOs, PageSetLabelRespDTO.class);
    }

    @Override
    public Long createPageSetLabel(PageSetLabelCreateDTO createDTO) {
        PageSetLabelDO pageSetLabelDO = BeanUtils.toBean(createDTO, PageSetLabelDO.class);
        pageSetLabelDO = appPageSetLabelDataRepository.insert(pageSetLabelDO);
        return pageSetLabelDO.getId();
    }

    @Override
    public void updatePageSetLabel(PageSetLabelUpdateDTO updateDTO) {
        PageSetLabelDO pageSetLabelDO = BeanUtils.toBean(updateDTO, PageSetLabelDO.class);
        appPageSetLabelDataRepository.update(pageSetLabelDO);
        return;
    }

    @Override
    public void deletePageSetLabel(Long id) {
        appPageSetLabelDataRepository.deleteById(id);
        return;
    }
}
