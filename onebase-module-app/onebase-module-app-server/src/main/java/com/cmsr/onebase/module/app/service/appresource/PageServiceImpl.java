package com.cmsr.onebase.module.app.service.appresource;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageDO;

import jakarta.annotation.Resource;

@Service
public class PageServiceImpl implements PageService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public PageRespDTO getPage(String pageCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "page_code", pageCode);
        PageDO pageDO = dataRepository.findOne(PageDO.class, configs);
        return BeanUtils.toBean(pageDO, PageRespDTO.class);
    }

    @Override
    public Long createPage(CreatePageDTO createPageDTO) {
        PageDO pageDO = BeanUtils.toBean(createPageDTO, PageDO.class);
        pageDO = dataRepository.insert(pageDO);
        return pageDO.getId();
    }

    @Override
    public Boolean deletePage(String code) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "page_code", code);
        dataRepository.deleteByConfig(PageDO.class, configs);
        return true;
    }
}
