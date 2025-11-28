package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private AppComponentRepository appComponentDataRepository;

    @Override
    public Boolean deleteComponent(Long id) {
        appComponentDataRepository.removeById(id);
        return true;
    }

    @Override
    public List<ComponentDTO> listComponent(Long pageId) {
        List<AppResourceComponentDO> componentDOS = appComponentDataRepository.findByPageId(pageId);
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

}
