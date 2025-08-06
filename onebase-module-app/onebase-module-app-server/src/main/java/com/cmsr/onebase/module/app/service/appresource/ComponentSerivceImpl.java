package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.ComponentRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.CreateComponentDTO;
import com.cmsr.onebase.module.app.dal.database.appresource.AppComponentRepository;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.ComponentDO;

import jakarta.annotation.Resource;

@Service
public class ComponentSerivceImpl implements ComponentSerivce {

    @Resource
    private AppComponentRepository appComponentDataRepository;

    @Override
    public ComponentRespDTO getComponent(Long id) {
        ComponentDO componentDO = appComponentDataRepository.findById(ComponentDO.class, id);
        return BeanUtils.toBean(componentDO, ComponentRespDTO.class);
    }

    @Override
    public Long createComponent(CreateComponentDTO createComponentDTO) {
        ComponentDO componentDO = BeanUtils.toBean(createComponentDTO, ComponentDO.class);
        componentDO = appComponentDataRepository.insert(componentDO);
        return componentDO.getId();
    }

    @Override
    public Boolean deleteComponent(Long id) {
        appComponentDataRepository.deleteById(ComponentDO.class, id);
        return true;
    }

    @Override
    public Boolean updateComponent(ComponentRespDTO componentRespDTO) {
        ComponentDO componentDO = BeanUtils.toBean(componentRespDTO, ComponentDO.class);
        componentDO = appComponentDataRepository.update(componentDO);
        return true;
    }
}
