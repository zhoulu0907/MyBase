package com.cmsr.onebase.module.app.service.appresource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.*;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.SavePageSetReqVO;
import com.cmsr.onebase.module.app.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.dal.database.appresource.*;
import com.cmsr.onebase.module.app.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.*;
import com.cmsr.onebase.module.app.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.util.PageUtils;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Validated
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

    @Override
    public String getPageSetCode(String menuCode) {
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByMenuCode(menuCode);
        return pageSetDO.getPageSetCode();
    }

    @Override
    public Long getAppId(String code) {
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByPageSetCode(code);
        String menuCode = pageSetDO.getMenuCode();
        MenuDO menuDO = appMenuRepository.findByMenuCode(menuCode);
        ApplicationDO applicationDO = appApplicationRepository.findOneByAppCode(menuDO.getApplicationCode());
        return applicationDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPageSet(CreatePageSetDTO createPageSetDTO) {
        PageSetDO pageSetDO = BeanUtils.toBean(createPageSetDTO, PageSetDO.class);
        pageSetDO.setPageSetCode(UUID.randomUUID().toString());
        pageSetDO = pageSetDataRepository.insert(pageSetDO);

        // 创建空的表单设计页面和列表设计页面
        String formPageCode = UUID.randomUUID().toString();
        String formPageName = pageSetDO.getPageSetName() + "_表单";
        String formRouterPath = formPageCode + "/form";
        String formPageType = "form";
        PageDO formPageDO = PageUtils.initPage(formPageCode, formPageName, formRouterPath, formPageType);
        pageDataRepository.insert(formPageDO);

        String listPageCode = UUID.randomUUID().toString();
        String listPageName = pageSetDO.getPageSetName() + "_列表";
        String listRouterPath = listPageCode + "/list";
        String listPageType = "list";
        PageDO listPageDO = PageUtils.initPage(listPageCode, listPageName, listRouterPath, listPageType);
        pageDataRepository.insert(listPageDO);

        PageSetPageDO formPageSetPageDO = new PageSetPageDO();
        formPageSetPageDO.setPageSetRef(pageSetDO.getPageSetCode());
        formPageSetPageDO.setPageType(formPageType);
        formPageSetPageDO.setPageRef(formPageDO.getPageCode());
        formPageSetPageDO.setIsDefault(true);
        formPageSetPageDO.setDefaultSeq(1);
        pageSetPageDataRepository.insert(formPageSetPageDO);

        PageSetPageDO listPageSetPageDO = new PageSetPageDO();
        listPageSetPageDO.setPageSetRef(pageSetDO.getPageSetCode());
        listPageSetPageDO.setPageType(listPageType);
        listPageSetPageDO.setPageRef(listPageDO.getPageCode());
        listPageSetPageDO.setIsDefault(true);
        listPageSetPageDO.setDefaultSeq(2);
        pageSetPageDataRepository.insert(listPageSetPageDO);

        return pageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageSet(String menuCode) {

        // 找到页面集
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByMenuCode(menuCode);

        // 删除页面集关联的页面
        List<PageSetPageDO> pageSetPageDOs = pageSetPageDataRepository.findByPageSetCode(pageSetDO.getPageSetCode());

        List<String> pageRefs = pageSetPageDOs.stream()
                .map(PageSetPageDO::getPageRef)
                .toList();

        // 删除页面集-页面关联表
        pageSetPageDataRepository.deleteByPageCode(pageSetDO.getPageSetCode());

        // 删除页面
        pageDataRepository.deletePageByCodes(pageRefs);

        // 删除页面集
        pageSetDataRepository.deletePageSetByMenuCode(menuCode);

        return;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyPageSet(CopyPageSetDTO copyPageSetDTO) {
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByMenuCode(copyPageSetDTO.getMenuCode());
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        // 复制页面集
        PageSetDO newPageSetDO = BeanUtils.toBean(pageSetDO, PageSetDO.class);
        newPageSetDO.setId(null);
        newPageSetDO.setPageSetCode(UUID.randomUUID().toString());
        newPageSetDO.setMenuCode(copyPageSetDTO.getNewMenuCode());
        pageSetDataRepository.insert(newPageSetDO);

        // 复制页面其余内容
        pageSetPageDataRepository.findByPageSetCode(pageSetDO.getPageSetCode()).forEach(pageSetPageDO -> {
            PageDO pageDO = pageDataRepository.selectPageByCode(pageSetPageDO.getPageRef());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }
            // 复制页面
            PageDO newPageDO = BeanUtils.toBean(pageDO, PageDO.class);
            newPageDO.setId(null);
            newPageDO.setPageCode(UUID.randomUUID().toString());
            pageDataRepository.insert(newPageDO);

            // 复制页面集-页面关联表
            PageSetPageDO newPageSetPageDO = pageSetPageDataRepository
                    .findByPageSetCodeAndPageRef(pageSetDO.getPageSetCode(), pageDO.getPageCode());
            newPageSetPageDO.setId(null);
            newPageSetPageDO.setPageSetRef(newPageSetDO.getPageSetCode());
            newPageSetPageDO.setPageRef(newPageDO.getPageCode());
            pageSetPageDataRepository.insert(newPageSetPageDO);

            // 复制组件
            componentDataRepository.findByPageCode(pageDO.getPageCode()).forEach(componentDO -> {
                ComponentDO newComponentDO = BeanUtils.toBean(componentDO, ComponentDO.class);
                newComponentDO.setId(null);
                newComponentDO.setPageCode(newPageDO.getPageCode());
                componentDataRepository.insert(newComponentDO);
            });

            // 复制页面元数据
            pageMetaDataRepository.findByPageID(pageDO.getId()).forEach(pageMetadataDO -> {
                PageMetadataDO newPageMetadataDO = BeanUtils.toBean(pageMetadataDO, PageMetadataDO.class);
                newPageMetadataDO.setId(null);
                newPageMetadataDO.setPageCode(newPageDO.getPageCode());
                pageMetaDataRepository.insert(newPageMetadataDO);
            });

            // 复制label
            pageSetLabelDataRepository.findByPageSetCode(pageSetDO.getPageSetCode()).forEach(pageSetLabelDO -> {
                PageSetLabelDO newPageSetLabelDO = BeanUtils.toBean(pageSetLabelDO, PageSetLabelDO.class);
                newPageSetLabelDO.setId(null);
                newPageSetLabelDO.setPagesetCode(newPageSetDO.getPageSetCode());
                pageSetLabelDataRepository.insert(newPageSetLabelDO);
            });

            // 复制RefRouter
            appPageRefRouterDataRepository.findPageRefRouterByPageCode(pageSetPageDO.getPageRef())
                    .forEach(pageRefRouterDO -> {
                        PageRefRouterDO newPageRefRouterDO = BeanUtils.toBean(pageRefRouterDO, PageRefRouterDO.class);
                        newPageRefRouterDO.setId(null);
                        newPageRefRouterDO.setPageCode(newPageDO.getPageCode());
                        appPageRefRouterDataRepository.insert(newPageRefRouterDO);
                    });
        });

        return newPageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean savePageSet(SavePageSetReqVO savePageSetReqVO) {

        savePageSetReqVO.getPages().forEach(page -> {
            PageDO pageDO = pageDataRepository.selectPageByCode(page.getPageCode());
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }
            pageDO.setPageName(page.getPageName());

            pageDataRepository.update(pageDO);

            // 删除已有的component
            componentDataRepository.deleteComponentByPageCode(pageDO.getPageCode());

            // 插入新的component
            page.getComponents().forEach(component -> {
                ComponentDO componentDO = BeanUtils.toBean(component, ComponentDO.class);
                componentDO.setInTable(false);
                componentDO.setPageCode(pageDO.getPageCode());
                componentDataRepository.insert(componentDO);
            });
        });

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO) {
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByPageSetCode(loadPageSetReqVO.getPageSetCode());

        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        // 读取页面集中的页面
        List<PageSetPageDO> pageSetPageDOs = pageSetPageDataRepository.findByPageSetCode(pageSetDO.getPageSetCode());

        List<PageDO> pageDOs = pageSetPageDOs.stream()
                .map(pageSetPageDO -> {
                    PageDO pageDO = pageDataRepository.selectPageByCode(pageSetPageDO.getPageRef());

                    if (pageDO == null) {
                        // 如果找不到对应的页面，记录错误并跳过
                        System.err.println("Warning: Page not found for pageRef: " + pageSetPageDO.getPageRef());
                        return null;
                    }
                    return pageDO;
                })
                .filter(pageDO -> pageDO != null) // 过滤掉null值
                .toList();

        LoadPageSetRespVO loadPageSetRespVO = new LoadPageSetRespVO();
        loadPageSetRespVO.setPageSetCode(pageSetDO.getPageSetCode());
        List<PageDTO> pageDTOs = new ArrayList<>();

        // 读取每个页面的组件和配置
        pageDOs.forEach(pageDO -> {
            List<ComponentDO> componentDOs = componentDataRepository.findByPageCode(pageDO.getPageCode());

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
    public PageSetRespDTO getPageSet(String code) {
        PageSetDO pageSetDO = pageSetDataRepository.findPageSetByPageSetCode(code);
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        PageSetRespDTO pageSetRespDTO = BeanUtils.toBean(pageSetDO, PageSetRespDTO.class);

        return pageSetRespDTO;
    }
}
