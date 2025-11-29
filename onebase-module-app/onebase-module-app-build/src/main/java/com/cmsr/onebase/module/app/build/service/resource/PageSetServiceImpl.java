package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.CopyPageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetRespDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.PageTypeSetEnum;
import com.cmsr.onebase.module.app.core.provider.resource.PageSetServiceProvider;
import com.cmsr.onebase.module.app.core.vo.resource.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Validated
@Slf4j
public class PageSetServiceImpl implements PageSetService {

    @Resource
    private WorkBenchPageSetService workBenchPageSetService;

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageRepository pageDataRepository;


    @Resource
    private AppComponentRepository componentDataRepository;

    @Resource
    private AppMenuRepository appMenuRepository;

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private PageSetServiceProvider pageSetServiceProvider;

    @Override
    public Long getPageSetId(Long menuId) {
        return pageSetServiceProvider.getPageSetId(menuId);
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

        AppResourcePagesetDO pageSetDO = BeanUtils.toBean(createPageSetDTO, AppResourcePagesetDO.class);
        pageSetDO.setPageSetCode(UUID.randomUUID().toString());
        pageSetDO.setPageSetType(createPageSetDTO.getPageSetType());
        pageSetDataRepository.save(pageSetDO);

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
        AppResourcePageDO formPageDO = PageUtils.initPage(pageSetDO.getId(), formPageName, formRouterPath, formPageType,
                formOpenViewMode);
        pageDataRepository.save(formPageDO);

        String listPageCode = UUID.randomUUID().toString();
        String listPageName = pageSetDO.getPageSetName() + "_列表";
        String listRouterPath = listPageCode + "/list";
        String listPageType = PageEnum.LIST.getValue();
        ;
        Boolean listOpenViewMode = false;
        AppResourcePageDO listPageDO = PageUtils.initPage(pageSetDO.getId(), listPageName, listRouterPath, listPageType,
                listOpenViewMode);
        pageDataRepository.save(listPageDO);

        AppResourcePagesetPageDO formPageSetPageDO = new AppResourcePagesetPageDO();
        formPageSetPageDO.setPageSetId(pageSetDO.getId());
        formPageSetPageDO.setPageType(formPageType);
        formPageSetPageDO.setPageId(formPageDO.getId());
        formPageSetPageDO.setIsDefault(1);
        formPageSetPageDO.setDefaultSeq(1);
        pageSetPageDataRepository.save(formPageSetPageDO);

        AppResourcePagesetPageDO listPageSetPageDO = new AppResourcePagesetPageDO();
        listPageSetPageDO.setPageSetId(pageSetDO.getId());
        listPageSetPageDO.setPageType(listPageType);
        listPageSetPageDO.setPageId(listPageDO.getId());
        listPageSetPageDO.setIsDefault(1);
        listPageSetPageDO.setDefaultSeq(2);
        pageSetPageDataRepository.save(listPageSetPageDO);

        return pageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageSet(Long menuId) {

        // 找到页面集
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.findPageSetByMenuId(menuId);

        // 删除页面集关联的页面
        List<AppResourcePagesetPageDO> pageSetPageDOs = pageSetPageDataRepository.findByPageSetId(pageSetDO.getId());

        List<Long> pageIds = pageSetPageDOs.stream()
                .map(AppResourcePagesetPageDO::getPageId)
                .toList();

        // 删除页面集-页面关联表
        pageSetPageDataRepository.deleteByPageSetId(pageSetDO.getId());

        // 删除页面
        pageDataRepository.removeByIds(pageIds);

        // 删除页面集
        pageSetDataRepository.deletePageSetByMenuId(menuId);

        return;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyPageSet(CopyPageSetDTO copyPageSetDTO) {
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.findPageSetByMenuId(copyPageSetDTO.getMenuId());
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        // 复制页面集
        AppResourcePagesetDO newPageSetDO = BeanUtils.toBean(pageSetDO, AppResourcePagesetDO.class);
        newPageSetDO.setId(null);
        newPageSetDO.setPageSetCode(UUID.randomUUID().toString());
        newPageSetDO.setMenuId(copyPageSetDTO.getNewMenuId());
        pageSetDataRepository.save(newPageSetDO);

        // 复制页面其余内容
        pageSetPageDataRepository.findByPageSetId(pageSetDO.getId()).forEach(pageSetPageDO -> {
            AppResourcePageDO pageDO = pageDataRepository.getById(pageSetPageDO.getPageId());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }
            // 复制页面
            AppResourcePageDO newPageDO = BeanUtils.toBean(pageDO, AppResourcePageDO.class);
            newPageDO.setId(null);
            pageDataRepository.save(newPageDO);

            // 复制页面集-页面关联表
            AppResourcePagesetPageDO newPageSetPageDO = pageSetPageDataRepository
                    .findByPageSetIdAndPageId(pageSetDO.getId(), pageDO.getId());
            newPageSetPageDO.setId(null);
            newPageSetPageDO.setPageSetId(newPageSetDO.getId());
            newPageSetPageDO.setPageId(newPageDO.getId());
            pageSetPageDataRepository.save(newPageSetPageDO);

            // 复制组件
            componentDataRepository.findByPageId(pageDO.getId()).forEach(componentDO -> {
                AppResourceComponentDO newComponentDO = BeanUtils.toBean(componentDO, AppResourceComponentDO.class);
                newComponentDO.setId(null);
                newComponentDO.setPageId(newPageDO.getId());
                componentDataRepository.save(newComponentDO);
            });
        });

        return newPageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean savePageSet(SavePageSetReqVO savePageSetReqVO) {

        AppResourcePagesetDO pageSetDO = pageSetDataRepository.getById(savePageSetReqVO.getId());

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

                AppResourcePageDO pageDO = PageUtils.initPage(savePageSetReqVO.getId(), pageName, routerPath, pageType,
                        openViewMode);
                pageDO.setId(page.getId());
                pageDO.setInteractionRules(page.getInteractionRules());

                pageDataRepository.save(pageDO);

                // 插入页面集合页面关系
                AppResourcePagesetPageDO pageSetPageDO = new AppResourcePagesetPageDO();
                pageSetPageDO.setPageSetId(savePageSetReqVO.getId());
                pageSetPageDO.setPageType(pageType);
                pageSetPageDO.setPageId(pageDO.getId());
                pageSetPageDO.setIsDefault(0);
                pageSetPageDO.setDefaultSeq(1);
                pageSetPageDataRepository.save(pageSetPageDO);

                page.setId(pageDO.getId());
            }

            AppResourcePageDO pageDO = pageDataRepository.getById(page.getId());
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

            pageDataRepository.updateById(finalPageDO);

            // 删除已有的component
            componentDataRepository.deleteComponentByPageId(finalPageDO.getId());

            // 插入新的component
            List<AppResourceComponentDO> componentDOs = new ArrayList<>();
            for (int idx = 0; idx < page.getComponents().size(); idx++) {
                AppResourceComponentDO componentDO = BeanUtils.toBean(page.getComponents().get(idx), AppResourceComponentDO.class);
                componentDO.setPageId(finalPageDO.getId());
                componentDO.setComponentIndex(idx);
                componentDOs.add(componentDO);
            }

            if (!componentDOs.isEmpty()) {
                componentDataRepository.saveBatch(componentDOs);
            }
        });
        return true;
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
