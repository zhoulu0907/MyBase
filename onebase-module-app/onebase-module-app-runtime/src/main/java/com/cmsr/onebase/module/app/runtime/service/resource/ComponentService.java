package com.cmsr.onebase.module.app.runtime.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ComponentService {

    List<ComponentDTO> listComponent(String pageUuid);

}
