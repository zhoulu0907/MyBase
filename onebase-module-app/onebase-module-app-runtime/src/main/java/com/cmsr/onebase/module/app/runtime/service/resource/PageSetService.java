package com.cmsr.onebase.module.app.runtime.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.CopyPageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetRespDTO;
import com.cmsr.onebase.module.app.core.vo.resource.*;

public interface PageSetService {

    Long getPageSetId(Long menuId);

    Long getAppId(Long pageSetId);

    String getMainMetadata(Long pageSetId);

    LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO);

    PageSetRespDTO getPageSet(Long pageSetId);

    ListPageSetRespVO listPageSet(ListPageSetReqVO listPageSetReqVO);
}
