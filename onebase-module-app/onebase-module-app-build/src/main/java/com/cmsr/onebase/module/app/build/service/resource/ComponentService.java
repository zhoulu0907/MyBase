package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ComponentService {

    Boolean deleteComponent(Long id);

    List<ComponentDTO> listComponent(Long pageId);

}
