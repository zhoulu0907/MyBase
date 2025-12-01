package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.*;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.ViewEnmu;
import com.cmsr.onebase.module.app.core.provider.resource.PageServiceProvider;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PageServiceImpl implements PageService {

    @Resource
    private AppPageRepository pageRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageRepository;

    @Resource
    private AppPageSetRepository pageSetRepository;

    @Resource(name = "appMenuRepository")
    private AppMenuRepository menuDataRepository;

    @Resource
    private PageServiceProvider pageServiceProvider;

    @Override
    public PageRespDTO getPage(Long pageId) {
        return pageServiceProvider.getPage(pageId);
    }

    @Override
    public Long createPage(CreatePageDTO createPageDTO) {
        AppResourcePageDO pageDO = BeanUtils.toBean(createPageDTO, AppResourcePageDO.class);
        pageRepository.save(pageDO);
        return pageDO.getId();
    }

    @Override
    public Long createPageView(CreatePageViewDTO createPageViewDTO) {
        String formPageCode = UUID.randomUUID().toString();
        String formPageName = createPageViewDTO.getViewName();
        String formRouterPath = formPageCode + "/form";
        String formPageType = PageEnum.FORM.getValue();
        Boolean formOpenViewMode = false;
        AppResourcePageDO formPageDO = PageUtils.initPage(createPageViewDTO.getPageSetId(), formPageName, formRouterPath, formPageType, formOpenViewMode);

        String viewType = createPageViewDTO.getViewType();

        if (viewType.equals(ViewEnmu.MIX.getValue())) {
            formPageDO.setEditViewMode(1);
            formPageDO.setDetailViewMode(1);
        }
        if (viewType.equals(ViewEnmu.EDIT.getValue())) {
            formPageDO.setEditViewMode(1);
        }
        if (viewType.equals(ViewEnmu.DETAIL.getValue())) {
            formPageDO.setDetailViewMode(1);
        }

        pageRepository.save(formPageDO);

        AppResourcePagesetPageDO formPageSetPageDO = new AppResourcePagesetPageDO();
        formPageSetPageDO.setPageSetId(createPageViewDTO.getPageSetUuid());
        formPageSetPageDO.setPageType(formPageType);
        formPageSetPageDO.setPageId(formPageDO.getId());
        formPageSetPageDO.setIsDefault(0);
        formPageSetPageDO.setDefaultSeq(1);

        pageSetPageRepository.save(formPageSetPageDO);

        return formPageDO.getId();
    }

    @Override
    public Boolean updatePageName(UpdatePageNameDTO updatePageNameDTO) {
        pageRepository.updatePageName(updatePageNameDTO.getPageId(), updatePageNameDTO.getPageName());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePage(Long pageId) {
        // 删除页面集关联的页面
        pageSetPageRepository.deleteByPageId(pageId);
        // 删除页面
        pageRepository.removeById(pageId);
        return true;
    }

    @Override
    public List<PageDTO> getFormPageListByAppId(Long appId) {
        return pageServiceProvider.getFormPageListByAppId(appId);
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
