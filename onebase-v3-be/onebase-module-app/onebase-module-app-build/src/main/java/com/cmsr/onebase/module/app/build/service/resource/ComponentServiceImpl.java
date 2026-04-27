package com.cmsr.onebase.module.app.build.service.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;
import com.cmsr.onebase.module.app.core.provider.resource.ComponentServiceProvider;

import lombok.Setter;

@Setter
@Service
public class ComponentServiceImpl implements ComponentService {

    @Autowired
    private ComponentServiceProvider componentServiceProvider;

    @Override
    public List<ComponentDTO> listComponentByPageId(Long pageId) {
        return componentServiceProvider.listComponent(pageId);
    }

    @Override
    public List<ComponentDTO> listComponentByPageUuid(String pageUuid) {
        return componentServiceProvider.listComponent(pageUuid);
    }

    @Override
    public List<ComponentDTO> listComponentForListPages(Long applicationId) {
        return componentServiceProvider.listComponentForListPages(applicationId);
    }

}
