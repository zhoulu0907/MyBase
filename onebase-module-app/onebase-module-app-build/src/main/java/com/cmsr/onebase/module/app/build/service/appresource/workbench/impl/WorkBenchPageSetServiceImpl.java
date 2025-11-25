package com.cmsr.onebase.module.app.build.service.appresource.workbench.impl;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.appresource.workbench.WorkBenchPageSetService;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.build.vo.appresource.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.build.vo.appresource.SavePageSetReqVO;
import com.cmsr.onebase.module.app.core.dal.database.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.*;
import com.cmsr.onebase.module.app.core.dal.database.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.PageSetPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.workbench.WorkBenchPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.workbench.WorkbenchComponentDO;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WorkBenchPageSetServiceImpl implements WorkBenchPageSetService {


    @Resource
    private AppWorkbenchPageRepository appWorkbenchPageRepository;

    @Resource

    private AppWorkbenchComponentRepository appWorkbenchComponentRepository;

    @Resource
    private WorkBenchPageSetService workBenchPageSetService;

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageRepository pageDataRepository;

    @Resource
    private AppWorkbenchPageRepository workbenchPageRepository;

    @Resource
    private AppComponentRepository componentDataRepository;
    @Resource
    private AppWorkbenchComponentRepository workbenchComponentRepository;

    @Resource
    private AppMenuRepository appMenuRepository;

    @Resource
    private AppApplicationRepository appApplicationRepository;


    @Override
    public void initWorkbenchPage(PageSetDO pageSetDO) {
        //1. 初始化工作台页面配置（空页面配置）
        WorkBenchPageDO workBenchPageDO = this.buildEmptyWorkbenchPage(pageSetDO);
        appWorkbenchPageRepository.insert(workBenchPageDO);

    }

    @Override
    public LoadPageSetRespVO loadWorkbenchPageSet(PageSetDO pageSetDO, List<PageSetPageDO> pageSetPageDOs) {

        List<WorkBenchPageDO> pageDOs = pageSetPageDOs.stream()
                .map(pageSetPageDO -> {
                    WorkBenchPageDO pageDO = appWorkbenchPageRepository.findById(pageSetPageDO.getPageId());

                    if (pageDO == null) {
                        // 如果找不到对应的页面，记录错误并跳过
                        System.err.println("Warning: Page not found for pageRef: " + pageSetPageDO.getPageId());
                        return null;
                    }
                    return pageDO;
                })
                .filter(pageDO -> pageDO != null) // 过滤掉null值
                .toList();

        LoadPageSetRespVO loadPageSetRespVO = new LoadPageSetRespVO();
        loadPageSetRespVO.setId(pageSetDO.getId());
        loadPageSetRespVO.setPageSetType(pageSetDO.getPageSetType());
        List<PageDTO> pageDTOs = new ArrayList<>();

        // 读取每个页面的组件和配置
        pageDOs.forEach(pageDO -> {
            List<WorkbenchComponentDO> componentDOs = appWorkbenchComponentRepository.findByPageId(pageDO.getId());

            PageDTO pageDTO = BeanUtils.toBean(pageDO, PageDTO.class);
            pageDTO.setComponents(componentDOs.stream()
                    .map(componentDO -> BeanUtils.toBean(componentDO, ComponentDTO.class))
                    .toList());
            pageDTOs.add(pageDTO);
        });

        loadPageSetRespVO.setPages(pageDTOs);
        loadPageSetRespVO.setMainMetadata(pageSetDO.getMainMetadata());

        return loadPageSetRespVO;
    }

    private WorkBenchPageDO buildEmptyWorkbenchPage(PageSetDO pageSetDO) {
        String pageName = StringUtils.isBlank(pageSetDO.getDisplayName()) ? pageSetDO.getPageSetName() : pageSetDO.getDisplayName();
        WorkBenchPageDO workBenchPageDO = new WorkBenchPageDO();
        workBenchPageDO.setPageSetId(pageSetDO.getId());
        workBenchPageDO.setPageName(pageName);
        workBenchPageDO.setTitle(pageName);
        workBenchPageDO.setPageType(PageEnum.WORKBENCH.getValue());
        //补全必填字段
        workBenchPageDO.setLayout("horizontal");
        workBenchPageDO.setWidth("auto");
        workBenchPageDO.setMargin("0");
        workBenchPageDO.setBackgroundColor("#FFFFFF");
        workBenchPageDO.setMainMetadata("{}");
        workBenchPageDO.setRouterPath("workbench/" + UUID.randomUUID());
        workBenchPageDO.setRouterName(pageName);
        workBenchPageDO.setRouterMetaTitle(pageName);

        return workBenchPageDO;
    }

    public void saveWorkbenchPage(SavePageSetReqVO savePageSetReqVO, PageSetDO pageSetDO) {

        savePageSetReqVO.getPages().forEach(page -> {
            if (Boolean.TRUE.equals(page.getCreated())) {
                // 插入新的视图
                String pageCode = UUID.randomUUID().toString();
                String pageName = page.getPageName();
                String routerPath = pageCode + "/form";
                String pageType = PageEnum.FORM.getValue();
                Boolean openViewMode = false;

                WorkBenchPageDO pageDO = PageUtils.initWorkbenchPage(savePageSetReqVO.getId(), pageName, routerPath, pageType,
                        openViewMode);
                pageDO.setId(page.getId());

                pageDO = workbenchPageRepository.insert(pageDO);

                // 插入页面集合页面关系
                PageSetPageDO pageSetPageDO = new PageSetPageDO();
                pageSetPageDO.setPageSetId(savePageSetReqVO.getId());
                pageSetPageDO.setPageType(pageType);
                pageSetPageDO.setPageId(pageDO.getId());
                pageSetPageDO.setIsDefault(0);
                pageSetPageDO.setDefaultSeq(1);
                pageSetPageDataRepository.save(pageSetPageDO);

                page.setId(pageDO.getId());
            }

            WorkBenchPageDO pageDO = workbenchPageRepository.findById(page.getId());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }

            final WorkBenchPageDO finalPageDO = pageDO;
            finalPageDO.setPageName(page.getPageName());
            finalPageDO.setEditViewMode(page.getEditViewMode());
            finalPageDO.setDetailViewMode(page.getDetailViewMode());
            finalPageDO.setIsDefaultEditViewMode(page.getIsDefaultEditViewMode());
            finalPageDO.setIsDefaultDetailViewMode(page.getIsDefaultDetailViewMode());
            finalPageDO.setIsLatestUpdated(page.getIsLatestUpdated());

            workbenchPageRepository.update(finalPageDO);

            // 删除已有的component
            componentDataRepository.deleteComponentByPageId(finalPageDO.getId());

            // 插入新的component
            List<WorkbenchComponentDO> componentDOs = new ArrayList<>();
            for (int idx = 0; idx < page.getComponents().size(); idx++) {
                WorkbenchComponentDO componentDO = BeanUtils.toBean(page.getComponents().get(idx), WorkbenchComponentDO.class);
                componentDO.setPageId(finalPageDO.getId());
                componentDO.setComponentIndex(idx);
                componentDOs.add(componentDO);
            }

            if (!componentDOs.isEmpty()) {
                workbenchComponentRepository.insertBatch(componentDOs);
            }
        });

    }


}
