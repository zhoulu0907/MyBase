package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dto.appresource.CopyPageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.PageTypeSetEnum;
import com.cmsr.onebase.module.app.core.provider.resource.PageSetServiceProvider;
import com.cmsr.onebase.module.app.core.vo.resource.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Service
@Validated
@Slf4j
public class PageSetServiceImpl implements PageSetService {

    @Autowired
    private WorkBenchPageSetService workBenchPageSetService;

    @Autowired
    private AppPageSetRepository pageSetRepository;

    @Autowired
    private AppPageRepository pageRepository;

    @Autowired
    private AppComponentRepository componentRepository;

    @Autowired
    private PageSetServiceProvider pageSetServiceProvider;

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Override
    public Long getPageSetIdByMenuId(Long menuId) {
        return pageSetServiceProvider.getPageSetIdByMenuId(menuId);
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
    @Transactional(rollbackFor = Exception.class)
    public String createPageSet(CreatePageSetDTO createPageSetDTO) {
        if (createPageSetDTO.getApplicationId() == null) {
            createPageSetDTO.setApplicationId(ApplicationManager.getApplicationId());
        }
        AppMenuDO appMenuDO = appMenuRepository.getById(createPageSetDTO.getMenuId());
        String menuUuid = appMenuDO.getMenuUuid();
        String pageSetUuid = UuidUtils.getUuid();
        AppResourcePagesetDO pageSetDO = BeanUtils.toBean(createPageSetDTO, AppResourcePagesetDO.class);
        pageSetDO.setMenuUuid(menuUuid);
        pageSetDO.setApplicationId(pageSetDO.getApplicationId());
        pageSetDO.setPageSetUuid(pageSetUuid);
        pageSetDO.setPageSetCode(UUID.randomUUID().toString());
        pageSetDO.setPageSetType(createPageSetDTO.getPageSetType());
        pageSetRepository.save(pageSetDO);

        /**
         * 借用表单贵宝地，拆出来工作台的逻辑：
         */
        if (PageTypeSetEnum.isWorkBenchType(createPageSetDTO.getPageSetType())) {
            /**
             * 创建工作台页面
             */
            workBenchPageSetService.initWorkbenchPage(pageSetDO);
            return pageSetDO.getPageSetCode();
        }

        // 创建空的表单设计页面和列表设计页面
        String formPageCode = UUID.randomUUID().toString();
        String formPageName = pageSetDO.getPageSetName() + "_表单";
        String formRouterPath = formPageCode + "/form";
        String formPageType = PageEnum.FORM.getValue();
        Boolean formOpenViewMode = true;
        AppResourcePageDO formPageDO = PageUtils.initPage(
                createPageSetDTO.getApplicationId(),
                pageSetUuid,
                formPageName,
                formRouterPath,
                formPageType,
                formOpenViewMode);
        pageRepository.save(formPageDO);

        String listPageCode = UUID.randomUUID().toString();
        String listPageName = pageSetDO.getPageSetName() + "_列表";
        String listRouterPath = listPageCode + "/list";
        String listPageType = PageEnum.LIST.getValue();
        Boolean listOpenViewMode = false;
        AppResourcePageDO listPageDO = PageUtils.initPage(
                createPageSetDTO.getApplicationId(),
                pageSetUuid,
                listPageName,
                listRouterPath,
                listPageType,
                listOpenViewMode);
        pageRepository.save(listPageDO);
        return pageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageSetByMenuId(Long menuId) {
        AppMenuDO appMenuDO = appMenuRepository.getById(menuId);
        // 找到页面集
        AppResourcePagesetDO pageSetDO = pageSetRepository.findPageSetByAppIdAndMenuUuid(appMenuDO.getApplicationId(), appMenuDO.getMenuUuid());
        // 删除页面集关联的页面
        List<Long> pageIds = pageRepository.findIdsByAppIdAndPageSetUuid(appMenuDO.getApplicationId(), pageSetDO.getPageSetUuid());
        // 删除页面集
        pageSetRepository.removeById(pageSetDO);
        // 删除页面
        pageRepository.removeByIds(pageIds);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyPageSet(CopyPageSetDTO copyPageSetDTO) {
        Long oldMenuUuid = copyPageSetDTO.getMenuId();
        Long newMenuUuid = copyPageSetDTO.getNewMenuId();
        AppResourcePagesetDO oldPageSetDO = pageSetRepository.getById(oldMenuUuid);
        if (oldPageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }
        Long applicationId = oldPageSetDO.getApplicationId();
        // 复制页面集
        String newPageSetUuid = UuidUtils.getUuid();
        AppResourcePagesetDO newPageSetDO = BeanUtils.toBean(oldPageSetDO, AppResourcePagesetDO.class);
        newPageSetDO.setId(newMenuUuid);
        newPageSetDO.setPageSetUuid(newPageSetUuid);
        newPageSetDO.setPageSetCode(UUID.randomUUID().toString());
        newPageSetDO.setMenuUuid(UuidUtils.getUuid());
        pageSetRepository.save(newPageSetDO);

        // 复制页面其余内容
        List<Long> pageIdList = pageRepository.findIdsByAppIdAndPageSetUuid(applicationId, oldPageSetDO.getPageSetUuid());
        if (CollectionUtils.isEmpty(pageIdList)) {
            return newPageSetDO.getPageSetCode();
        }
        for (Long pageId : pageIdList) {
            AppResourcePageDO pageDO = pageRepository.getById(pageId);
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }
            String newPageUuid = UuidUtils.getUuid();
            AppResourcePageDO newPageDO = BeanUtils.toBean(pageDO, AppResourcePageDO.class);
            newPageDO.setId(null);
            newPageDO.setApplicationId(applicationId);
            newPageDO.setPageUuid(newPageUuid);
            newPageDO.setPageSetUuid(newPageSetUuid);
            pageRepository.save(newPageDO);

            List<AppResourceComponentDO> componentDOs = componentRepository.findByAppIdAndPageUuid(applicationId, pageDO.getPageUuid());
            if (CollectionUtils.isEmpty(componentDOs)) {
                continue;
            }
            for (AppResourceComponentDO componentDO : componentDOs) {
                String newComponentUuid = UuidUtils.getUuid();
                AppResourceComponentDO newComponentDO = BeanUtils.toBean(componentDO, AppResourceComponentDO.class);
                newComponentDO.setId(null);
                newComponentDO.setApplicationId(applicationId);
                newComponentDO.setComponentUuid(newComponentUuid);
                newComponentDO.setPageUuid(newPageDO.getPageUuid());
                componentRepository.save(newComponentDO);
            }
        }
        return newPageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean savePageSet(SavePageSetReqVO savePageSetReqVO) {
        AppResourcePagesetDO pageSetDO = pageSetRepository.getById(savePageSetReqVO.getId());
        Long applicationId = pageSetDO.getApplicationId();
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }
        if (PageTypeSetEnum.isWorkBenchType(pageSetDO.getPageSetType())) {
            /**
             * 保存工作台页面配置
             */
            workBenchPageSetService.saveWorkbenchPage(savePageSetReqVO, pageSetDO);
            return true;
        }
        savePageSetReqVO.getPages().forEach(page -> {
            if (Boolean.TRUE.equals(page.getCreated())) {
                // 插入新的视图
                String pageCode = UUID.randomUUID().toString();
                String pageName = page.getPageName();
                String routerPath = pageCode + "/form";
                String pageType = PageEnum.FORM.getValue();
                Boolean openViewMode = false;

                AppResourcePageDO pageDO = PageUtils.initPage(
                        applicationId,
                        pageSetDO.getPageSetUuid(),
                        pageName,
                        routerPath,
                        pageType,
                        openViewMode);
                pageDO.setId(page.getId());
                pageDO.setInteractionRules(page.getInteractionRules());
                pageRepository.save(pageDO);

                page.setId(pageDO.getId());
            }

            AppResourcePageDO pageDO = pageRepository.getById(page.getId());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }

            final AppResourcePageDO finalPageDO = pageDO;
            finalPageDO.setPageName(page.getPageName());
            finalPageDO.setEditViewMode(page.getEditViewMode());
            finalPageDO.setDetailViewMode(page.getDetailViewMode());
            finalPageDO.setIsDefaultEditViewMode(page.getIsDefaultEditViewMode());
            finalPageDO.setIsDefaultDetailViewMode(page.getIsDefaultDetailViewMode());
            finalPageDO.setIsLatestUpdated(page.getIsLatestUpdated());
            finalPageDO.setInteractionRules(page.getInteractionRules());

            pageRepository.updateById(finalPageDO);

            // 删除已有的component
            componentRepository.deleteComponentByPageUuid(applicationId, finalPageDO.getPageUuid());
            // 插入新的component
            if (CollectionUtils.isEmpty(page.getComponents())) {
                return;
            }
            List<AppResourceComponentDO> componentDOs = new ArrayList<>();
            for (int idx = 0; idx < page.getComponents().size(); idx++) {
                AppResourceComponentDO componentDO = BeanUtils.toBean(page.getComponents().get(idx), AppResourceComponentDO.class);
                componentDO.setPageUuid(finalPageDO.getPageUuid());
                componentDO.setComponentIndex(idx);
                componentDOs.add(componentDO);
            }

            if (!componentDOs.isEmpty()) {
                componentRepository.saveBatch(componentDOs);
            }
        });
        return true;
    }


    @Override
    public LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO) {
        return pageSetServiceProvider.loadPageSet(loadPageSetReqVO);
    }

    @Override
    public ListPageSetRespVO listPageSet(ListPageSetReqVO listPageSetReqVO) {
        return pageSetServiceProvider.listPageSet(listPageSetReqVO);
    }

}
