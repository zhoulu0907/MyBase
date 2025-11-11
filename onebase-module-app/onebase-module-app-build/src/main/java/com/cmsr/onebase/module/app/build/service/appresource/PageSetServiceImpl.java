package com.cmsr.onebase.module.app.build.service.appresource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.service.AppCommonService;
import com.cmsr.onebase.module.app.build.util.PageUtils;
import com.cmsr.onebase.module.app.build.vo.appresource.*;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.appresource.*;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.dto.appresource.*;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.appresource.PageEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.PageTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class PageSetServiceImpl implements PageSetService {

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageRepository pageDataRepository;

    @Resource
    private AppComponentRepository componentDataRepository;

    @Resource
    private AppPageMetaRepository pageMetaDataRepository;

    @Resource
    private AppPageSetLabelRepository pageSetLabelDataRepository;

    @Resource
    private AppPageRefRouterRepository appPageRefRouterDataRepository;

    @Resource
    private AppMenuRepository appMenuRepository;

    @Resource
    private AppApplicationRepository appApplicationRepository;

    @Resource
    private AppCommonService appCommonService;

    @Override
    public Long getPageSetId(Long menuId) {
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByMenuId(menuId);
        return pageSetDO.getId();
    }

    @Override
    public Long getAppId(Long pageSetId) {
        PageSetDO pageSetDO = pageSetDataRepository.findById(pageSetId);
        Long menuId = pageSetDO.getMenuId();
        MenuDO menuDO = appMenuRepository.findById(menuId);
        return menuDO.getApplicationId();
    }

    @Override
    public String getMainMetadata(Long pageSetId) {
        PageSetDO pageSetDO = pageSetDataRepository.findById(pageSetId);
        return pageSetDO.getMainMetadata();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPageSet(CreatePageSetDTO createPageSetDTO) {
        PageSetDO pageSetDO = BeanUtils.toBean(createPageSetDTO, PageSetDO.class);
        pageSetDO.setPageSetCode(UUID.randomUUID().toString());
        pageSetDO.setPageSetType(createPageSetDTO.getPageSetType());
        pageSetDO = pageSetDataRepository.insert(pageSetDO);

        // 创建空的表单设计页面和列表设计页面
        String formPageCode = UUID.randomUUID().toString();
        String formPageName = pageSetDO.getPageSetName() + "_表单";
        String formRouterPath = formPageCode + "/form";
        String formPageType = PageEnum.FORM.getValue();
        Boolean formOpenViewMode = true;
        PageDO formPageDO = PageUtils.initPage(pageSetDO.getId(), formPageName, formRouterPath, formPageType,
                formOpenViewMode);
        pageDataRepository.insert(formPageDO);

        String listPageCode = UUID.randomUUID().toString();
        String listPageName = pageSetDO.getPageSetName() + "_列表";
        String listRouterPath = listPageCode + "/list";
        String listPageType = PageEnum.LIST.getValue();
        ;
        Boolean listOpenViewMode = false;
        PageDO listPageDO = PageUtils.initPage(pageSetDO.getId(), listPageName, listRouterPath, listPageType,
                listOpenViewMode);
        pageDataRepository.insert(listPageDO);

        PageSetPageDO formPageSetPageDO = new PageSetPageDO();
        formPageSetPageDO.setPageSetId(pageSetDO.getId());
        formPageSetPageDO.setPageType(formPageType);
        formPageSetPageDO.setPageId(formPageDO.getId());
        formPageSetPageDO.setIsDefault(1);
        formPageSetPageDO.setDefaultSeq(1);
        pageSetPageDataRepository.insert(formPageSetPageDO);

        PageSetPageDO listPageSetPageDO = new PageSetPageDO();
        listPageSetPageDO.setPageSetId(pageSetDO.getId());
        listPageSetPageDO.setPageType(listPageType);
        listPageSetPageDO.setPageId(listPageDO.getId());
        listPageSetPageDO.setIsDefault(1);
        listPageSetPageDO.setDefaultSeq(2);
        pageSetPageDataRepository.insert(listPageSetPageDO);

        return pageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageSet(Long menuId) {

        // 找到页面集
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByMenuId(menuId);

        // 删除页面集关联的页面
        List<PageSetPageDO> pageSetPageDOs = pageSetPageDataRepository.findByPageSetId(pageSetDO.getId());

        List<Long> pageIds = pageSetPageDOs.stream()
                .map(PageSetPageDO::getPageId)
                .toList();

        // 删除页面集-页面关联表
        pageSetPageDataRepository.deleteByPageSetId(pageSetDO.getId());

        // 删除页面
        pageDataRepository.deletePageByIds(pageIds);

        // 删除页面集
        pageSetDataRepository.deletePageSetByMenuId(menuId);

        return;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyPageSet(CopyPageSetDTO copyPageSetDTO) {
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByMenuId(copyPageSetDTO.getMenuId());
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        // 复制页面集
        PageSetDO newPageSetDO = BeanUtils.toBean(pageSetDO, PageSetDO.class);
        newPageSetDO.setId(null);
        newPageSetDO.setPageSetCode(UUID.randomUUID().toString());
        newPageSetDO.setMenuId(copyPageSetDTO.getNewMenuId());
        pageSetDataRepository.insert(newPageSetDO);

        // 复制页面其余内容
        pageSetPageDataRepository.findByPageSetId(pageSetDO.getId()).forEach(pageSetPageDO -> {
            PageDO pageDO = pageDataRepository.findById(pageSetPageDO.getPageId());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }
            // 复制页面
            PageDO newPageDO = BeanUtils.toBean(pageDO, PageDO.class);
            newPageDO.setId(null);
            pageDataRepository.insert(newPageDO);

            // 复制页面集-页面关联表
            PageSetPageDO newPageSetPageDO = pageSetPageDataRepository
                    .findByPageSetIdAndPageId(pageSetDO.getId(), pageDO.getId());
            newPageSetPageDO.setId(null);
            newPageSetPageDO.setPageSetId(newPageSetDO.getId());
            newPageSetPageDO.setPageId(newPageDO.getId());
            pageSetPageDataRepository.insert(newPageSetPageDO);

            // 复制组件
            componentDataRepository.findByPageId(pageDO.getId()).forEach(componentDO -> {
                ComponentDO newComponentDO = BeanUtils.toBean(componentDO, ComponentDO.class);
                newComponentDO.setId(null);
                newComponentDO.setPageId(newPageDO.getId());
                componentDataRepository.insert(newComponentDO);
            });

            // 复制页面元数据
            pageMetaDataRepository.findByPageId(pageDO.getId()).forEach(pageMetadataDO -> {
                PageMetadataDO newPageMetadataDO = BeanUtils.toBean(pageMetadataDO, PageMetadataDO.class);
                newPageMetadataDO.setId(null);
                newPageMetadataDO.setPageId(newPageDO.getId());
                pageMetaDataRepository.insert(newPageMetadataDO);
            });

            // 复制label
            pageSetLabelDataRepository.findByPageSetId(pageSetDO.getId()).forEach(pageSetLabelDO -> {
                PageSetLabelDO newPageSetLabelDO = BeanUtils.toBean(pageSetLabelDO, PageSetLabelDO.class);
                newPageSetLabelDO.setId(null);
                newPageSetLabelDO.setPageSetId(newPageSetDO.getId());
                pageSetLabelDataRepository.insert(newPageSetLabelDO);
            });

            // 复制RefRouter
            appPageRefRouterDataRepository.findByPageId(pageSetPageDO.getPageId())
                    .forEach(pageRefRouterDO -> {
                        PageRefRouterDO newPageRefRouterDO = BeanUtils.toBean(pageRefRouterDO, PageRefRouterDO.class);
                        newPageRefRouterDO.setId(null);
                        newPageRefRouterDO.setPageId(newPageDO.getId());
                        appPageRefRouterDataRepository.insert(newPageRefRouterDO);
                    });
        });

        return newPageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean savePageSet(SavePageSetReqVO savePageSetReqVO) {

        savePageSetReqVO.getPages().forEach(page -> {
            if (Boolean.TRUE.equals(page.getCreated())) {
                // 插入新的视图
                String pageCode = UUID.randomUUID().toString();
                String pageName = page.getPageName();
                String routerPath = pageCode + "/form";
                String pageType = PageEnum.FORM.getValue();
                Boolean openViewMode = false;

                PageDO pageDO = PageUtils.initPage(savePageSetReqVO.getId(), pageName, routerPath, pageType,
                        openViewMode);
                pageDO.setId(page.getId());

                pageDO = pageDataRepository.insert(pageDO);

                // 插入页面集合页面关系
                PageSetPageDO pageSetPageDO = new PageSetPageDO();
                pageSetPageDO.setPageSetId(savePageSetReqVO.getId());
                pageSetPageDO.setPageType(pageType);
                pageSetPageDO.setPageId(pageDO.getId());
                pageSetPageDO.setIsDefault(0);
                pageSetPageDO.setDefaultSeq(1);
                pageSetPageDataRepository.insert(pageSetPageDO);

                page.setId(pageDO.getId());
            }

            PageDO pageDO = pageDataRepository.findById(page.getId());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }

            final PageDO finalPageDO = pageDO;
            finalPageDO.setPageName(page.getPageName());
            finalPageDO.setEditViewMode(page.getEditViewMode());
            finalPageDO.setDetailViewMode(page.getDetailViewMode());
            finalPageDO.setIsDefaultEditViewMode(page.getIsDefaultEditViewMode());
            finalPageDO.setIsDefaultDetailViewMode(page.getIsDefaultDetailViewMode());
            finalPageDO.setIsLatestUpdated(page.getIsLatestUpdated());

            pageDataRepository.update(finalPageDO);

            // 删除已有的component
            componentDataRepository.deleteComponentByPageId(finalPageDO.getId());

            // 插入新的component
            List<ComponentDO> componentDOs = new ArrayList<>();
            for (int idx = 0; idx < page.getComponents().size(); idx++) {
                ComponentDO componentDO = BeanUtils.toBean(page.getComponents().get(idx), ComponentDO.class);
                componentDO.setPageId(finalPageDO.getId());
                componentDO.setComponentIndex(idx);
                componentDOs.add(componentDO);
            }

            if (!componentDOs.isEmpty()) {
                componentDataRepository.insertBatch(componentDOs);
            }
        });

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO) {
        PageSetDO pageSetDO = pageSetDataRepository.findById(loadPageSetReqVO.getId());

        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        // 读取页面集中的页面
        List<PageSetPageDO> pageSetPageDOs = pageSetPageDataRepository.findByPageSetId(pageSetDO.getId());

        List<PageDO> pageDOs = pageSetPageDOs.stream()
                .map(pageSetPageDO -> {
                    PageDO pageDO = pageDataRepository.findById(pageSetPageDO.getPageId());

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
            List<ComponentDO> componentDOs = componentDataRepository.findByPageId(pageDO.getId());

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

    @Override
    public PageSetRespDTO getPageSet(Long pageSetId) {
        PageSetDO pageSetDO = pageSetDataRepository.findById(pageSetId);
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        PageSetRespDTO pageSetRespDTO = BeanUtils.toBean(pageSetDO, PageSetRespDTO.class);

        return pageSetRespDTO;
    }

    @Override
    public ListPageSetRespVO listPageSet(ListPageSetReqVO listPageSetReqVO) {
        ListPageSetRespVO respVO = new ListPageSetRespVO();
        respVO.setPageSets(new ArrayList<>());

        Integer pageSetType = listPageSetReqVO.getPageSetType();

        if (pageSetType != null) {
            PageTypeEnum.validate(pageSetType);
        }

        // 查询应用菜单ID
        ApplicationDO applicationDO = appCommonService.validateApplicationExist(listPageSetReqVO.getApplicationId());
        List<MenuDO> menuDOS = appMenuRepository.findByApplicationId(applicationDO.getId());

        if (CollectionUtils.isEmpty(menuDOS)) {
            return respVO;
        }

        List<Long> menuIds = menuDOS.stream()
                .map(MenuDO::getId)
                .collect(Collectors.toList());

        List<PageSetDO> pageSetDOs;

        if (pageSetType != null) {
            pageSetDOs = pageSetDataRepository.findByMenuIdAndType(menuIds, pageSetType);
        } else {
            pageSetDOs = pageSetDataRepository.findByMenuId(menuIds);
        }

        respVO.setPageSets(pageSetDOs.stream()
                .map(pageSetDO -> BeanUtils.toBean(pageSetDO, ListPageSetRespVO.PageSetVO.class))
                .toList());

        return respVO;
    }
}
