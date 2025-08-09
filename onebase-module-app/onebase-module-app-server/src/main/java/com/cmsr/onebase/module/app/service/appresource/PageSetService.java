package com.cmsr.onebase.module.app.service.appresource;

import com.cmsr.onebase.module.app.api.appresource.dto.CopyPageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetRespDTO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.SavePageSetReqVO;

public interface PageSetService {

    String getPageSetCode(Long menuID);

    Long getAppId(String code);

    String createPageSet(CreatePageSetDTO createPageSetDTO);

    void deletePageSet(Long menuId);

    String copyPageSet(CopyPageSetDTO copyPageSetDTO);

    Boolean savePageSet(SavePageSetReqVO savePageSetReqVO);

    LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO);

    PageSetRespDTO getPageSet(String code);
}
