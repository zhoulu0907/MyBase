package com.cmsr.onebase.module.app.service.appresource;

import com.cmsr.onebase.module.app.api.appresource.dto.ComponentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ComponentSerivce {

    Boolean deleteComponent(Long id);

    List<ComponentDTO> listComponent(Long pageId);


}
