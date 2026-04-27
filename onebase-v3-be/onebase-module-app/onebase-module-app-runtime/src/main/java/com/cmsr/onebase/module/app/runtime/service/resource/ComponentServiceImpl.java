package com.cmsr.onebase.module.app.runtime.service.resource;

import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;
import com.cmsr.onebase.module.app.core.provider.resource.ComponentServiceProvider;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private ComponentServiceProvider componentServiceProvider;

    @Override
    public List<ComponentDTO> listComponent(Long pageId) {
        return componentServiceProvider.listComponent(pageId);
    }

}
