package com.cmsr.onebase.module.app.service.appresource;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelCreateDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelUpdateDTO;

@Service
public interface PageSetLabelService {

    List<PageSetLabelRespDTO> getLabelsByPageSetCode(String pagesetCode);

    Long createPageSetLabel(PageSetLabelCreateDTO createDTO);

    void updatePageSetLabel(PageSetLabelUpdateDTO updateDTO);

    void deletePageSetLabel(Long id);
}
