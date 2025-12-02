package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageViewDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.UpdatePageNameDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.ViewEnmu;
import com.cmsr.onebase.module.app.core.provider.resource.PageServiceProvider;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Setter
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private AppPageRepository pageRepository;

    @Autowired
    private AppPageSetRepository pageSetRepository;

    @Autowired
    private AppMenuRepository menuDataRepository;

    @Autowired
    private PageServiceProvider pageServiceProvider;

    @Override
    public Long createPageView(CreatePageViewDTO createPageViewDTO) {
        AppResourcePagesetDO pagesetDO = pageSetRepository.getById(createPageViewDTO.getPageSetId());
        String pageSetUuid = pagesetDO.getPageSetUuid();

        Long applicationId = ApplicationManager.getApplicationId();
        String formPageName = createPageViewDTO.getViewName();
        //TODO formPageCode formRouterPath 这两个可以删除
        String formPageCode = UUID.randomUUID().toString();
        String formRouterPath = formPageCode + "/form";
        String formPageType = PageEnum.FORM.getValue();
        Boolean formOpenViewMode = false;
        AppResourcePageDO formPageDO = PageUtils.initPage(
                applicationId,
                pageSetUuid,
                formPageName,
                formRouterPath,
                formPageType,
                formOpenViewMode);

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
        return formPageDO.getId();
    }

    @Override
    public Boolean updatePageName(UpdatePageNameDTO updatePageNameDTO) {
        pageRepository.updatePageName(updatePageNameDTO.getPageId(), updatePageNameDTO.getPageName());
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
