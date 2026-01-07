package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ComponentService {

    List<ComponentDTO> listComponentByPageId(Long pageId);

    List<ComponentDTO> listComponentByPageUuid(String pageUuid);

}
