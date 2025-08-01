package com.cmsr.onebase.module.app.service.appresource;

import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetRespDTO;

public interface PageSetService {

    Long createPageSet(CreatePageSetDTO createPageSetDTO);

    void deletePageSet(String code);

    PageSetRespDTO getPageSet(String code);
}
