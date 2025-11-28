package com.cmsr.onebase.module.app.build.service.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.build.vo.appresource.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.build.vo.appresource.SavePageSetReqVO;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchComponentDO;
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
    public void initWorkbenchPage(AppResourcePagesetDO pageSetDO) {
        //1. 初始化工作台页面配置（空页面配置）
        AppResourceWorkbenchPageDO workBenchPageDO = this.buildEmptyWorkbenchPage(pageSetDO);
        appWorkbenchPageRepository.save(workBenchPageDO);

        //2. 创建页面集和页面的关联关系
        AppResourcePagesetPageDO pageSetPageDO = new AppResourcePagesetPageDO();
        pageSetPageDO.setPageSetId(pageSetDO.getId());
        pageSetPageDO.setPageType(PageEnum.WORKBENCH.getValue());
        pageSetPageDO.setPageId(workBenchPageDO.getId());
        pageSetPageDO.setIsDefault(1);
        pageSetPageDO.setDefaultSeq(1);
        pageSetPageDataRepository.save(pageSetPageDO);
    }

    @Override
    public LoadPageSetRespVO loadWorkbenchPageSet(AppResourcePagesetDO pageSetDO, List<AppResourcePagesetPageDO> pageSetPageDOs) {

        List<AppResourceWorkbenchPageDO> pageDOs;

        // 兼容旧数据：如果页面集-页面关联表中没有数据，直接通过pageSetId查询工作台页面
        if (pageSetPageDOs == null || pageSetPageDOs.isEmpty()) {
            pageDOs = appWorkbenchPageRepository.findByPageSetId(pageSetDO.getId());
        } else {
            pageDOs = pageSetPageDOs.stream()
                    .map(pageSetPageDO -> {
                        AppResourceWorkbenchPageDO pageDO = appWorkbenchPageRepository.getById(pageSetPageDO.getPageId());

                        if (pageDO == null) {
                            // 如果找不到对应的页面，记录错误并跳过
                            System.err.println("Warning: Page not found for pageRef: " + pageSetPageDO.getPageId());
                            return null;
                        }
                        return pageDO;
                    })
                    .filter(pageDO -> pageDO != null) // 过滤掉null值
                    .toList();
        }

        LoadPageSetRespVO loadPageSetRespVO = new LoadPageSetRespVO();
        loadPageSetRespVO.setId(pageSetDO.getId());
        loadPageSetRespVO.setPageSetType(pageSetDO.getPageSetType());
        List<PageDTO> pageDTOs = new ArrayList<>();

        // 读取每个页面的组件和配置
        pageDOs.forEach(pageDO -> {
            List<AppResourceWorkbenchComponentDO> componentDOs = appWorkbenchComponentRepository.findByPageId(pageDO.getId());

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

    private AppResourceWorkbenchPageDO buildEmptyWorkbenchPage(AppResourcePagesetDO pageSetDO) {
        String pageName = StringUtils.isBlank(pageSetDO.getDisplayName()) ? pageSetDO.getPageSetName() : pageSetDO.getDisplayName();
        AppResourceWorkbenchPageDO workBenchPageDO = new AppResourceWorkbenchPageDO();
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

    public void saveWorkbenchPage(SavePageSetReqVO savePageSetReqVO, AppResourcePagesetDO pageSetDO) {

        savePageSetReqVO.getPages().forEach(page -> {
            if (Boolean.TRUE.equals(page.getCreated())) {
                // 插入新的视图
                String pageCode = UUID.randomUUID().toString();
                String pageName = page.getPageName();
                String routerPath = pageCode + "/form";
                String pageType = PageEnum.FORM.getValue();
                Boolean openViewMode = false;

                AppResourceWorkbenchPageDO pageDO = PageUtils.initWorkbenchPage(savePageSetReqVO.getId(), pageName, routerPath, pageType,
                        openViewMode);
                pageDO.setId(page.getId());

                workbenchPageRepository.save(pageDO);

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

            AppResourceWorkbenchPageDO pageDO = workbenchPageRepository.getById(page.getId());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }

            final AppResourceWorkbenchPageDO finalPageDO = pageDO;
            finalPageDO.setPageName(page.getPageName());
            finalPageDO.setEditViewMode(page.getEditViewMode());
            finalPageDO.setDetailViewMode(page.getDetailViewMode());
            finalPageDO.setIsDefaultEditViewMode(page.getIsDefaultEditViewMode());
            finalPageDO.setIsDefaultDetailViewMode(page.getIsDefaultDetailViewMode());
            finalPageDO.setIsLatestUpdated(page.getIsLatestUpdated());

            workbenchPageRepository.updateById(finalPageDO);

            // 删除已有的component
            componentDataRepository.deleteComponentByPageId(finalPageDO.getId());

            // 插入新的component
            List<AppResourceWorkbenchComponentDO> componentDOs = new ArrayList<>();
            for (int idx = 0; idx < page.getComponents().size(); idx++) {
                AppResourceWorkbenchComponentDO componentDO = BeanUtils.toBean(page.getComponents().get(idx), AppResourceWorkbenchComponentDO.class);
                componentDO.setPageId(finalPageDO.getId());
                componentDO.setComponentIndex(idx);
                componentDOs.add(componentDO);
            }

            if (!componentDOs.isEmpty()) {
                workbenchComponentRepository.saveBatch(componentDOs);
            }
        });

    }


}
