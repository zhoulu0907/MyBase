package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.ComponentRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.CreateComponentDTO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.ComponentDO;

import jakarta.annotation.Resource;

@Service
public class ComponentSerivceImpl implements ComponentSerivce {

    @Resource
    private DataRepository dataRepository;

    @Override
    public ComponentRespDTO getComponent(Long id) {
        ComponentDO componentDO = dataRepository.findById(ComponentDO.class, id);
        return BeanUtils.toBean(componentDO, ComponentRespDTO.class);
    }

    @Override
    public Long createComponent(CreateComponentDTO createComponentDTO) {
        ComponentDO componentDO = BeanUtils.toBean(createComponentDTO, ComponentDO.class);
        componentDO = dataRepository.insert(componentDO);
        return componentDO.getId();
    }

    @Override
    public Boolean deleteComponent(Long id) {
        dataRepository.deleteById(ComponentDO.class, id);
        return true;
    }

    @Override
    public Boolean updateComponent(ComponentRespDTO componentRespDTO) {
        ComponentDO componentDO = BeanUtils.toBean(componentRespDTO, ComponentDO.class);
        componentDO = dataRepository.update(componentDO);
        return true;
    }
}
