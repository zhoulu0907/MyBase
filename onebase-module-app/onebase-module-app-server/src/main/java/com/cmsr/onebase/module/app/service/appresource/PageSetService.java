package com.cmsr.onebase.module.app.service.appresource;

import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetRespDTO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.SavePageSetVO;

public interface PageSetService {

    Long createPageSet(CreatePageSetDTO createPageSetDTO);

    void deletePageSet(String code);

    Boolean savePageSet(SavePageSetVO savePageSetVO);

    LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO);

    PageSetRespDTO getPageSet(String code);
}
