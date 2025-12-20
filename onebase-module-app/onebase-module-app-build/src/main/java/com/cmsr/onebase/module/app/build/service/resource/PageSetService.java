package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dto.resource.CopyPageSetDTO;
import com.cmsr.onebase.module.app.core.dto.resource.CreatePageSetDTO;
import com.cmsr.onebase.module.app.core.vo.resource.*;

public interface PageSetService {

    Long getPageSetIdByMenuId(Long menuId);

    Long getAppId(Long pageSetId);

    String getMainMetadata(Long pageSetId);

    String createPageSet(CreatePageSetDTO createPageSetDTO);

    void deletePageSetByMenu(AppMenuDO menuDO);

    void deletePageSetByMenuId(Long menuId);

    String copyPageSet(CopyPageSetDTO copyPageSetDTO);

    Boolean savePageSet(SavePageSetReqVO savePageSetReqVO);

    LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO);

    ListPageSetRespVO listPageSet(ListPageSetReqVO listPageSetReqVO);
}
