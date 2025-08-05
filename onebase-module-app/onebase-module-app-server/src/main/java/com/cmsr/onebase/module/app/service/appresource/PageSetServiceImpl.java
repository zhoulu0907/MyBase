package com.cmsr.onebase.module.app.service.appresource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.dto.ComponentDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetRespDTO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.SavePageSetReqVO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.ComponentDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.dal.dataobject.appresource.PageSetPageDO;
import com.cmsr.onebase.module.app.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.util.PageUtils;

import jakarta.annotation.Resource;

@Service
@Validated
public class PageSetServiceImpl implements PageSetService {

    @Resource
    private DataRepository dataRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPageSet(CreatePageSetDTO createPageSetDTO) {
        PageSetDO pageSetDO = BeanUtils.toBean(createPageSetDTO, PageSetDO.class);
        pageSetDO.setPageSetCode(UUID.randomUUID().toString());
        pageSetDO = dataRepository.insert(pageSetDO);

        // 创建空的表单设计页面和列表设计页面
        String formPageCode = UUID.randomUUID().toString();
        String formPageName = pageSetDO.getPageSetName() + "_表单";
        String formRouterPath = formPageCode + "/form";
        String formPageType = "form";
        PageDO formPageDO = PageUtils.initPage(formPageCode, formPageName, formRouterPath, formPageType);
        dataRepository.insert(formPageDO);

        String listPageCode = UUID.randomUUID().toString();
        String listPageName = pageSetDO.getPageSetName() + "_列表";
        String listRouterPath = listPageCode + "/list";
        String listPageType = "list";
        PageDO listPageDO = PageUtils.initPage(listPageCode, listPageName, listRouterPath, listPageType);
        dataRepository.insert(listPageDO);

        PageSetPageDO formPageSetPageDO = new PageSetPageDO();
        formPageSetPageDO.setPageSetRef(pageSetDO.getPageSetCode());
        formPageSetPageDO.setPageType(formPageType);
        formPageSetPageDO.setPageRef(formPageDO.getPageCode());
        formPageSetPageDO.setIsDefault(true);
        formPageSetPageDO.setDefaultSeq(1);
        dataRepository.insert(formPageSetPageDO);

        PageSetPageDO listPageSetPageDO = new PageSetPageDO();
        listPageSetPageDO.setPageSetRef(pageSetDO.getPageSetCode());
        listPageSetPageDO.setPageType(listPageType);
        listPageSetPageDO.setPageRef(listPageDO.getPageCode());
        listPageSetPageDO.setIsDefault(true);
        listPageSetPageDO.setDefaultSeq(2);
        dataRepository.insert(listPageSetPageDO);

        return pageSetDO.getPageSetCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageSet(String pageSetCode) {

        // 删除页面集关联的页面
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_ref", pageSetCode);

        List<PageSetPageDO> pageSetPageDOs = dataRepository.findAll(PageSetPageDO.class, configs);
        List<String> pageRefs = pageSetPageDOs.stream()
                .map(PageSetPageDO::getPageRef)
                .toList();

        // 删除页面集-页面关联表
        configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_ref", pageSetCode);
        dataRepository.deleteByConfig(PageSetPageDO.class, configs);

        // 删除页面
        configs = new DefaultConfigStore();
        configs.and(Compare.IN, "page_code", pageRefs);
        dataRepository.deleteByConfig(PageDO.class, configs);

        // 删除页面集
        configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_code", pageSetCode);
        dataRepository.deleteByConfig(PageSetDO.class, configs);

        return;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean savePageSet(SavePageSetReqVO savePageSetReqVO) {

        savePageSetReqVO.getPages().forEach(page -> {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "page_code", page.getPageCode());
            PageDO pageDO = dataRepository.findOne(PageDO.class, configs);
            if (pageDO == null) {
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
            }
            pageDO.setPageName(page.getPageName());

            dataRepository.update(pageDO);

            // 删除已有的component
            configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "page_id", pageDO.getId());
            dataRepository.deleteByConfig(ComponentDO.class, configs);

            // 插入新的component
            page.getComponents().forEach(component -> {
                ComponentDO componentDO = BeanUtils.toBean(component, ComponentDO.class);
                componentDO.setInTable(false);

                componentDO.setPageId(pageDO.getId());
                dataRepository.insert(componentDO);
            });
        });

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_code", loadPageSetReqVO.getPageSetCode());
        PageSetDO pageSetDO = dataRepository.findOne(PageSetDO.class, configs);

        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        // 读取页面集中的页面
        configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_ref", pageSetDO.getPageSetCode());
        List<PageSetPageDO> pageSetPageDOs = dataRepository.findAll(PageSetPageDO.class, configs);

        List<PageDO> pageDOs = pageSetPageDOs.stream()
                .map(pageSetPageDO -> {
                    ConfigStore cfg = new DefaultConfigStore();
                    cfg.and(Compare.EQUAL, "page_code", pageSetPageDO.getPageRef());
                    PageDO pageDO = dataRepository.findOne(PageDO.class, cfg);

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
            ConfigStore cfg = new DefaultConfigStore();
            cfg.and(Compare.EQUAL, "page_id", pageDO.getId());
            List<ComponentDO> componentDOs = dataRepository.findAll(ComponentDO.class, cfg);

            PageDTO pageDTO = BeanUtils.toBean(pageDO, PageDTO.class);
            pageDTO.setComponents(componentDOs.stream()
                    .map(componentDO -> BeanUtils.toBean(componentDO, ComponentDTO.class))
                    .toList());
            pageDTOs.add(pageDTO);
        });

        loadPageSetRespVO.setPages(pageDTOs);

        return loadPageSetRespVO;
    }

    @Override
    public PageSetRespDTO getPageSet(String code) {
        ConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, "pageset_code", code);
        PageSetDO pageSetDO = dataRepository.findOne(PageSetDO.class, configs);
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        PageSetRespDTO pageSetRespDTO = BeanUtils.toBean(pageSetDO, PageSetRespDTO.class);

        return pageSetRespDTO;
    }
}
