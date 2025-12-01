package com.cmsr.onebase.module.app.runtime.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.PageSetRespDTO;
import com.cmsr.onebase.module.app.core.provider.resource.PageSetServiceProvider;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetRespVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
public class PageSetServiceImpl implements PageSetService {

    @Resource
    private PageSetServiceProvider pageSetServiceProvider;

    @Override
    public String getPageSetId(String menuUuid) {
        return pageSetServiceProvider.getPageSetIdByMenuUuid(menuUuid);
    }

    @Override
    public Long getAppId(Long pageSetId) {
        return pageSetServiceProvider.getAppId(pageSetId);
    }

    @Override
    public String getMainMetadata(Long pageSetId) {
        return pageSetServiceProvider.getMainMetadata(pageSetId);
    }

    @Override
    public LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO) {
        return pageSetServiceProvider.loadPageSet(loadPageSetReqVO);
    }

    @Override
    public PageSetRespDTO getPageSet(Long pageSetId) {
        return pageSetServiceProvider.getPageSet(pageSetId);
    }

    @Override
    public ListPageSetRespVO listPageSet(ListPageSetReqVO listPageSetReqVO) {
        return pageSetServiceProvider.listPageSet(listPageSetReqVO);
    }
}
