package com.cmsr.onebase.module.app.runtime.service.resource;

import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageRespDTO;
import com.cmsr.onebase.module.app.core.provider.resource.PageServiceProvider;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {

    @Resource
    private PageServiceProvider pageServiceProvider;

    @Override
    public PageRespDTO getPage(Long pageId) {
        return pageServiceProvider.getPage(pageId);
    }

    @Override
    public List<PageDTO> getPageListByAppId(Long appId) {
        return pageServiceProvider.getPageListByAppId(appId);

    }

    @Override
    public String getMetadataByPageId(Long pageId) {
        return pageServiceProvider.getMetadataByPageId(pageId);
    }

    @Override
    public List<PageDTO> listPageView(Long pageSetId) {
        return pageServiceProvider.listPageView(pageSetId);
    }

}
