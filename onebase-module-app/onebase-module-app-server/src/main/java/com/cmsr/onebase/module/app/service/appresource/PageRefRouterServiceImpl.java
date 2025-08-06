package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageRefRouterDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRefRouterRespDTO;
import com.cmsr.onebase.module.app.dal.database.appresource.AppPageRefRouterRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageRefRouterDO;

import jakarta.annotation.Resource;

@Service
public class PageRefRouterServiceImpl implements PageRefRouterService {

    @Resource
    private AppPageRefRouterRepository dataRepository;

    @Override
    public PageRefRouterRespDTO getPageRefRouter(Long id) {
        PageRefRouterDO pageRefRouterDO = dataRepository.findById(id);
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
        dataRepository.deleteById(id);
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
        List<PageRefRouterDO> pageRefRouterDOList = dataRepository.findPageRefRouterByPageCode(pageCode);
        return BeanUtils.toBean(pageRefRouterDOList, PageRefRouterRespDTO.class);
    }
}
