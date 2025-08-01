package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelCreateDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelUpdateDTO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetLabelDO;

import jakarta.annotation.Resource;

@Service
public class PageSetLabelServiceImpl implements PageSetLabelService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public List<PageSetLabelRespDTO> getLabelsByPageSetCode(String pagesetCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_code", pagesetCode);
        List<PageSetLabelDO> pageSetLabelDOs = dataRepository.findAll(PageSetLabelDO.class, configs);
        return BeanUtils.toBean(pageSetLabelDOs, PageSetLabelRespDTO.class);
    }

    @Override
    public Long createPageSetLabel(PageSetLabelCreateDTO createDTO) {
        PageSetLabelDO pageSetLabelDO = BeanUtils.toBean(createDTO, PageSetLabelDO.class);
        pageSetLabelDO = dataRepository.insert(pageSetLabelDO);
        return pageSetLabelDO.getId();
    }

    @Override
    public void updatePageSetLabel(PageSetLabelUpdateDTO updateDTO) {
        PageSetLabelDO pageSetLabelDO = BeanUtils.toBean(updateDTO, PageSetLabelDO.class);
        dataRepository.update(pageSetLabelDO);
    }

    @Override
    public void deletePageSetLabel(Long id) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "id", id);
        dataRepository.deleteByConfig(PageSetLabelDO.class, configs);
    }
}
