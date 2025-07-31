package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageRefRouterDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRefRouterRespDTO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageRefRouterDO;

import jakarta.annotation.Resource;

@Service
public class PageRefRouterServiceImpl implements PageRefRouterService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public PageRefRouterRespDTO getPageRefRouter(Long id) {
        PageRefRouterDO pageRefRouterDO = dataRepository.findById(PageRefRouterDO.class, id);
        return BeanUtils.toBean(pageRefRouterDO, PageRefRouterRespDTO.class);
    }

    @Override
    public Long createPageRefRouter(CreatePageRefRouterDTO createPageRefRouterDTO) {
        PageRefRouterDO pageRefRouterDO = BeanUtils.toBean(createPageRefRouterDTO, PageRefRouterDO.class);
        pageRefRouterDO = dataRepository.insert(pageRefRouterDO);
        return pageRefRouterDO.getId();
    }

    @Override
    public Boolean deletePageRefRouter(Long id) {
        dataRepository.deleteById(PageRefRouterDO.class, id);
        return true;
    }

    @Override
    public Boolean updatePageRefRouter(PageRefRouterRespDTO pageRefRouterRespDTO) {
        PageRefRouterDO pageRefRouterDO = BeanUtils.toBean(pageRefRouterRespDTO, PageRefRouterDO.class);
        pageRefRouterDO = dataRepository.update(pageRefRouterDO);
        return true;
    }

    @Override
    public List<PageRefRouterRespDTO> getPageRefRouterList(String pageCode) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "page_ref", pageCode);
        List<PageRefRouterDO> pageRefRouterDOList = dataRepository.findAll(PageRefRouterDO.class, configs);
        return BeanUtils.toBean(pageRefRouterDOList, PageRefRouterRespDTO.class);
    }
}
