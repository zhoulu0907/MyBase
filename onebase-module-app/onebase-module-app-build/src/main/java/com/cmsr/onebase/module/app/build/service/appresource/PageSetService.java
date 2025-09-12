package com.cmsr.onebase.module.app.build.service.appresource;

import com.cmsr.onebase.module.app.core.dto.appresource.CopyPageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetRespDTO;
import com.cmsr.onebase.module.app.build.vo.appresource.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.build.vo.appresource.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.build.vo.appresource.SavePageSetReqVO;

public interface PageSetService {

    Long getPageSetId(Long menuId);

    Long getAppId(Long pageSetId);

    String getMainMetadata(Long pageSetId);

    String createPageSet(CreatePageSetDTO createPageSetDTO);

    void deletePageSet(Long menuId);

    String copyPageSet(CopyPageSetDTO copyPageSetDTO);

    Boolean savePageSet(SavePageSetReqVO savePageSetReqVO);

    LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO);

    PageSetRespDTO getPageSet(Long pageSetId);
}
