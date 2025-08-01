package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageComponentDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageComponentRespDTO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageComponentDO;

import jakarta.annotation.Resource;

@Service
public class PageComponentServiceImpl implements PageComponentService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public PageComponentRespDTO getPageComponent(Long id) {
        PageComponentDO pageComponentDO = dataRepository.findById(PageComponentDO.class, id);
        return BeanUtils.toBean(pageComponentDO, PageComponentRespDTO.class);
    }

    @Override
    public Long createPageComponent(CreatePageComponentDTO createPageComponentDTO) {
        PageComponentDO pageComponentDO = BeanUtils.toBean(createPageComponentDTO, PageComponentDO.class);
        pageComponentDO = dataRepository.insert(pageComponentDO);
        return pageComponentDO.getId();
    }

    @Override
    public Boolean deletePageComponent(Long id) {
        dataRepository.deleteById(PageComponentDO.class, id);
        return true;
    }

    @Override
    public Boolean updatePageComponent(PageComponentRespDTO pageComponentRespDTO) {
        PageComponentDO pageComponentDO = BeanUtils.toBean(pageComponentRespDTO, PageComponentDO.class);
        pageComponentDO = dataRepository.update(pageComponentDO);
        return true;
    }

    @Override
    public List<PageComponentRespDTO> getPageComponentList(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "page_id", pageId);
        List<PageComponentDO> pageComponentDOList = dataRepository.findAll(PageComponentDO.class, configs);
        return BeanUtils.toBean(pageComponentDOList, PageComponentRespDTO.class);
    }

}
