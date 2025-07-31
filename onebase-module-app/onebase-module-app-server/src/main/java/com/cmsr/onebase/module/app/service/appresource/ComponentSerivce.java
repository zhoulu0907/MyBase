package com.cmsr.onebase.module.app.service.appresource;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.api.appresource.dto.ComponentRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.CreateComponentDTO;

@Service
public interface ComponentSerivce {

    ComponentRespDTO getComponent(Long id);

    Long createComponent(CreateComponentDTO createComponentDTO);

    Boolean deleteComponent(Long id);

    Boolean updateComponent(ComponentRespDTO componentRespDTO);
}
