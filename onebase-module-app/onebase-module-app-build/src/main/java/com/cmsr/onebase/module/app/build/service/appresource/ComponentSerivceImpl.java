package com.cmsr.onebase.module.app.build.service.appresource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.ComponentDO;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentSerivceImpl implements ComponentSerivce {

    @Resource
    private AppComponentRepository appComponentDataRepository;

    @Override
    public Boolean deleteComponent(Long id) {
        appComponentDataRepository.removeById(id);
        return true;
    }

    @Override
    public List<ComponentDTO> listComponent(Long pageId) {
        List<ComponentDO> componentDOS = appComponentDataRepository.findByPageId(pageId);
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

}
