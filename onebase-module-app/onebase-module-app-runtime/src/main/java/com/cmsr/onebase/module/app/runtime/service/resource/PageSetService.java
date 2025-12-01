package com.cmsr.onebase.module.app.runtime.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.PageSetRespDTO;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetRespVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetRespVO;

public interface PageSetService {

    String getPageSetId(String menuUuid);

    Long getAppId(Long pageSetId);

    String getMainMetadata(Long pageSetId);

    LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO);

    PageSetRespDTO getPageSet(Long pageSetId);

    ListPageSetRespVO listPageSet(ListPageSetReqVO listPageSetReqVO);
}
