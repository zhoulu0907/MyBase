package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.*;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.ViewEnmu;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public PageRespDTO getPage(Long pageId) {
        AppResourcePageDO pageDO = pageRepository.getById(pageId);
        if (pageDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
        }
        return BeanUtils.toBean(pageDO, PageRespDTO.class);
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
        formPageSetPageDO.setPageSetId(createPageViewDTO.getPageSetId());
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
        List<AppMenuDO> menuDOList = menuDataRepository.findByApplicationId(appId);
        List<Long> menuIdList = menuDOList.stream().map(AppMenuDO::getId).toList();
        if (CollectionUtils.isEmpty(menuIdList)) {
            return Collections.emptyList();
        }
        List<AppResourcePagesetDO> pageSetDoList = pageSetRepository.findByMenuIds(menuIdList);
        if (CollectionUtils.isEmpty(pageSetDoList)) {
            return Collections.emptyList();
        }
        List<Long> pageSetIdList = pageSetDoList.stream()
                .map(AppResourcePagesetDO::getId)
                .collect(Collectors.toList());
        List<AppResourcePageDO> pageDOList = pageRepository.findAllFormPageByPageSetIds(pageSetIdList);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);
        return pageDTOList;

    }

    @Override
    public String getMetadataByPageId(Long pageId) {
        AppResourcePagesetPageDO pageSetPageDO = pageSetPageRepository.findByPageId(pageId);
        AppResourcePagesetDO pageSetDO = pageSetRepository.getById(pageSetPageDO.getPageSetId());

        return pageSetDO.getMainMetadata();
    }

    @Override
    public List<PageDTO> listPageView(Long pageSetId) {
        List<AppResourcePageDO> pageDOList = pageRepository.findAllFormPageByPageSetId(pageSetId);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);
        return pageDTOList;
    }


}
