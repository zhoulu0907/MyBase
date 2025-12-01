package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.*;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageSetRespDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.appresource.PageTypeEnum;
import com.cmsr.onebase.module.app.core.enums.appresource.PageTypeSetEnum;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetRespVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PageSetServiceProvider {

    @Resource
    private WorkBenchPageSetServiceProvider workBenchPageSetService;

    @Resource
    private AppPageSetRepository pageSetDataRepository;

    @Resource
    private AppPageSetPageRepository pageSetPageDataRepository;

    @Resource
    private AppPageRepository pageRepository;

    @Resource
    private AppComponentRepository componentDataRepository;

    @Resource
    private AppMenuRepository appMenuRepository;

    public String getPageSetIdByMenuUuid(String menuUuid) {
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.findPageSetByMenuUuid(menuUuid);
        return pageSetDO.getPageSetUuid();
    }


    public Long getAppId(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.getById(pageSetId);
        String menuUuid = pageSetDO.getMenuUuid();
        // TODO: refactor this
        AppMenuDO menuDO = appMenuRepository.getById(menuUuid);
        return menuDO.getApplicationId();
    }


    public String getMainMetadata(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.getById(pageSetId);
        return pageSetDO.getMainMetadata();
    }


    public LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO) {
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.getById(loadPageSetReqVO.getId());

        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        // 读取页面集中的页面
        List<AppResourcePagesetPageDO> pageSetPageDOs = pageSetPageDataRepository.findByPageSetUuid(pageSetDO.getPageSetUuid());

        if (PageTypeSetEnum.isWorkBenchType(pageSetDO.getPageSetType())) {
            /**
             * 加载工作台页面配置
             */
            return workBenchPageSetService.loadWorkbenchPageSet(pageSetDO, pageSetPageDOs);
        }

        List<AppResourcePageDO> pageDOs = pageSetPageDOs.stream()
                .map(pageSetPageDO -> {
                    AppResourcePageDO pageDO = pageRepository.getByUuid(pageSetPageDO.getPageUuid());

                    if (pageDO == null) {
                        // 如果找不到对应的页面，记录错误并跳过
                        log.warn("Page not found for pageRef: {}", pageSetPageDO.getPageUuid());
                        return null;
                    }
                    return pageDO;
                })
                .filter(Objects::nonNull) // 过滤掉null值
                .toList();

        LoadPageSetRespVO loadPageSetRespVO = new LoadPageSetRespVO();
        loadPageSetRespVO.setId(pageSetDO.getId());
        loadPageSetRespVO.setPageSetType(pageSetDO.getPageSetType());
        List<PageDTO> pageDTOs = new ArrayList<>();

        // 读取每个页面的组件和配置
        pageDOs.forEach(pageDO -> {
            List<AppResourceComponentDO> componentDOs = componentDataRepository.findByPageUuid(pageDO.getPageUuid());

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


    public PageSetRespDTO getPageSet(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = pageSetDataRepository.getById(pageSetId);
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }

        PageSetRespDTO pageSetRespDTO = BeanUtils.toBean(pageSetDO, PageSetRespDTO.class);

        return pageSetRespDTO;
    }


    public ListPageSetRespVO listPageSet(ListPageSetReqVO listPageSetReqVO) {
        ListPageSetRespVO respVO = new ListPageSetRespVO();
        respVO.setPageSets(new ArrayList<>());

        Integer pageSetType = listPageSetReqVO.getPageSetType();

        if (pageSetType != null) {
            PageTypeEnum.validate(pageSetType);
        }

        // 查询应用菜单ID
        List<AppMenuDO> menuDOS = appMenuRepository.findByApplicationId(listPageSetReqVO.getApplicationId());

        if (CollectionUtils.isEmpty(menuDOS)) {
            return respVO;
        }

        List<String> menuUuids = menuDOS.stream().map(AppMenuDO::getMenuUuid).toList();
        List<AppResourcePagesetDO> pageSetDOs;

        if (pageSetType != null) {
            pageSetDOs = pageSetDataRepository.findByMenuUuidAndType(menuUuids, pageSetType);
        } else {
            pageSetDOs = pageSetDataRepository.findByMenuUuids(menuUuids);
        }

        respVO.setPageSets(pageSetDOs.stream()
                .map(pageSetDO -> BeanUtils.toBean(pageSetDO, ListPageSetRespVO.PageSetVO.class))
                .toList());

        return respVO;
    }
}
