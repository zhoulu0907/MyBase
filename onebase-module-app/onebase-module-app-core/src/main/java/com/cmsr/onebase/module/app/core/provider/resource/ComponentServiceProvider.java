package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
public class ComponentServiceProvider {

    @Autowired
    private AppComponentRepository appComponentDataRepository;

    public List<ComponentDTO> listComponent(String pageUuid) {
        List<AppResourceComponentDO> componentDOS = appComponentDataRepository.findByPageUuid(pageUuid);
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

}
