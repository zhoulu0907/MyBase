package com.cmsr.onebase.module.app.core.impl.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.AppMenuRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.AppPagesetRespDTO;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyang
 * @date 2025-11-14
 */
@Setter
@Service
public class AppResourceImpl implements AppResourceApi {

    @Autowired
    private AppPageRepository pageRepository;

    @Autowired
    private AppPageSetRepository pageSetRepository;

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Override
    public List<PageRespDTO> findPageListByPageSetId(Long pageSetId) {
        List<PageRespDTO> pageRespDTOs = new ArrayList<>();
        if (pageSetId == null) {
            throw new IllegalArgumentException("页面集ID不能为空");
        }
        AppResourcePagesetDO pagesetDO = pageSetRepository.getById(pageSetId);
        // 读取页面集中的页面
        List<AppResourcePageDO> pageDOS = pageRepository.findAllFormPageByAppIdAndPageSetUuid(pagesetDO.getApplicationId(), pagesetDO.getPageSetUuid());
        return BeanUtils.toBean(pageDOS, PageRespDTO.class);
    }

    @Override
    public List<PageRespDTO> findPageListByPageSetUuidAndAppId(String pageSetUuid, Long applicationId) {
        if (StringUtils.isBlank(pageSetUuid)) {
            throw new IllegalArgumentException("页面集UUID不能为空");
        }
        // 读取页面集中的页面
        List<AppResourcePageDO> pageDOS = pageRepository.findAllFormPageByAppIdAndPageSetUuid(applicationId, pageSetUuid);
        return BeanUtils.toBean(pageDOS, PageRespDTO.class);
    }

    @Override
    public AppPagesetRespDTO getPageSetByMenuUuidAndAppId(String menuUuid, Long applicationId) {
        AppResourcePagesetDO pagesetDO = pageSetRepository.findPageSetByAppIdAndMenuUuid(applicationId, menuUuid);

        if (pagesetDO != null) {
            return BeanUtils.toBean(pagesetDO, AppPagesetRespDTO.class);
        }

        return null;
    }

    @Override
    public AppMenuRespDTO getAppMenuById(Long menuId) {
        AppMenuDO appMenuDO = appMenuRepository.getById(menuId);

        if (appMenuDO != null) {
            return BeanUtils.toBean(appMenuDO, AppMenuRespDTO.class);
        }

        return null;
    }

    @Override
    public AppMenuRespDTO getAppMenuByUuidAndAppId(String menuUuid, Long applicationId) {
        AppMenuDO appMenuDO = appMenuRepository.findByAppIdAndMenuUuid(applicationId, menuUuid);

        if (appMenuDO != null) {
            return BeanUtils.toBean(appMenuDO, AppMenuRespDTO.class);
        }

        return null;
    }
}