package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
public class ComponentServiceProvider {

    @Resource
    private AppComponentRepository appComponentDataRepository;

    public List<ComponentDTO> listComponent(Long pageId) {
        List<AppResourceComponentDO> componentDOS = appComponentDataRepository.findByPageId(pageId);
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

}
