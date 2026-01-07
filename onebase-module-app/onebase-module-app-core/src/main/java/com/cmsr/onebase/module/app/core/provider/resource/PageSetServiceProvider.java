package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;
import com.cmsr.onebase.module.app.core.dto.resource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.resource.PageSetRespDTO;
import com.cmsr.onebase.module.app.core.enums.resource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.resource.PageTypeEnum;
import com.cmsr.onebase.module.app.core.enums.resource.PageTypeSetEnum;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetRespVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetRespVO;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Service
@Slf4j
public class PageSetServiceProvider {

    @Resource
    private WorkBenchPageSetServiceProvider workBenchPageSetService;

    @Resource
    private AppPageSetRepository appPageSetRepository;

    @Resource
    private AppPageRepository pageRepository;

    @Resource
    private AppComponentRepository componentDataRepository;

    @Resource
    private AppMenuRepository appMenuRepository;

    public Long getPageSetIdByMenuId(Long menuId) {
        AppMenuDO appMenuDO = appMenuRepository.getById(menuId);
        if (appMenuDO == null) {
            return null;
        }
        AppResourcePagesetDO pagesetDO = appPageSetRepository.findPageSetByAppIdAndMenuUuid(
                appMenuDO.getApplicationId(),
                appMenuDO.getMenuUuid());
        return pagesetDO.getId();
    }


    public Long getAppId(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = appPageSetRepository.getById(pageSetId);
        return pageSetDO.getApplicationId();
    }


    public String getMainMetadata(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = appPageSetRepository.getById(pageSetId);
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }
        return pageSetDO.getMainMetadata();
    }


    public LoadPageSetRespVO loadPageSet(LoadPageSetReqVO loadPageSetReqVO) {
        AppResourcePagesetDO pageSetDO = appPageSetRepository.getById(loadPageSetReqVO.getId());
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_SET_NOT_EXIST);
        }
        Long applicationId = pageSetDO.getApplicationId();
        // 读取页面集中的页面
        List<Long> pageIdList = pageRepository.findIdsByAppIdAndPageSetUuid(applicationId, pageSetDO.getPageSetUuid());

        if (PageTypeSetEnum.isWorkBenchType(pageSetDO.getPageSetType())) {
            /**
             * 加载工作台页面配置
             */
            return workBenchPageSetService.loadWorkbenchPageSet(pageSetDO);
        }

        List<AppResourcePageDO> pageDOs = pageIdList.stream()
                .map(pageId -> {
                    AppResourcePageDO pageDO = pageRepository.getById(pageId);
                    if (pageDO == null) {
                        // 如果找不到对应的页面，记录错误并跳过
                        log.warn("Page not found for pageRef: {}", pageId);
                        return null;
                    }
                    return pageDO;
                })
                .filter(Objects::nonNull)
                .toList();

        LoadPageSetRespVO loadPageSetRespVO = new LoadPageSetRespVO();
        loadPageSetRespVO.setId(pageSetDO.getId());
        loadPageSetRespVO.setPageSetType(pageSetDO.getPageSetType());

        // 读取每个页面的组件和配置
        List<PageDTO> pageDTOs = new ArrayList<>();
        pageDOs.forEach(pageDO -> {
            List<AppResourceComponentDO> componentDOs = componentDataRepository.findByAppIdAndPageUuid(applicationId, pageDO.getPageUuid());
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
        AppResourcePagesetDO pageSetDO = appPageSetRepository.getById(pageSetId);
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
        Long applicationId = listPageSetReqVO.getApplicationId();
        // 查询应用菜单ID
        List<AppMenuDO> menuDOS = appMenuRepository.findByApplicationId(applicationId);

        if (CollectionUtils.isEmpty(menuDOS)) {
            return respVO;
        }

        List<String> menuUuids = menuDOS.stream().map(AppMenuDO::getMenuUuid).toList();
        List<AppResourcePagesetDO> pageSetDOs;

        if (pageSetType != null) {
            pageSetDOs = appPageSetRepository.findByMenuUuidAndType(applicationId, menuUuids, pageSetType);
        } else {
            pageSetDOs = appPageSetRepository.findByMenuUuids(applicationId, menuUuids);
        }

        respVO.setPageSets(pageSetDOs.stream()
                .map(pageSetDO -> BeanUtils.toBean(pageSetDO, ListPageSetRespVO.PageSetVO.class))
                .toList());

        return respVO;
    }
}
