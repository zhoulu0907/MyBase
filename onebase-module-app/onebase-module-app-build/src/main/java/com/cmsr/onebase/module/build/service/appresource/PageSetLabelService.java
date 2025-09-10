package com.cmsr.onebase.module.build.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.core.dto.appresource.PageSetLabelCreateDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetLabelRespDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetLabelUpdateDTO;

@Service
public interface PageSetLabelService {

    List<PageSetLabelRespDTO> getLabelsByPageSetId(Long pageSetId);

    Long createPageSetLabel(PageSetLabelCreateDTO createDTO);

    void updatePageSetLabel(PageSetLabelUpdateDTO updateDTO);

    void deletePageSetLabel(Long id);
}
