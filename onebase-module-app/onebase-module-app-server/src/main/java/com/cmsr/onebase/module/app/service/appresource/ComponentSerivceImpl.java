package com.cmsr.onebase.module.app.service.appresource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.ComponentDTO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.ComponentDO;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.dal.database.appresource.AppComponentRepository;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class ComponentSerivceImpl implements ComponentSerivce {

    @Resource
    private AppComponentRepository appComponentDataRepository;

    @Override
    public Boolean deleteComponent(Long id) {
        appComponentDataRepository.deleteById(id);
        return true;
    }

    @Override
    public List<ComponentDTO> listComponent(Long pageId) {
        List<ComponentDO> componentDOS= appComponentDataRepository.findByPageId(pageId);
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

}
