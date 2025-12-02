package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import com.cmsr.onebase.module.app.core.provider.resource.ComponentServiceProvider;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
public class ComponentServiceImpl implements ComponentService {

    @Autowired
    private ComponentServiceProvider componentServiceProvider;

    @Override
    public List<ComponentDTO> listComponent(Long pageId) {
        return componentServiceProvider.listComponent(pageId);
    }

}
