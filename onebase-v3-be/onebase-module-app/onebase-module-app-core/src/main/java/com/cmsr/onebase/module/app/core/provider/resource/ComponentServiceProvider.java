package com.cmsr.onebase.module.app.core.provider.resource;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dto.resource.ComponentDTO;
import com.cmsr.onebase.module.app.core.enums.resource.AppResourceErrorCodeConstants;

import lombok.Setter;

@Setter
@Service
public class ComponentServiceProvider {

    @Autowired
    private AppComponentRepository appComponentDataRepository;

    @Autowired
    private AppPageRepository pageRepository;

    public List<ComponentDTO> listComponent(Long pageId) {
        AppResourcePageDO pageDO = pageRepository.getById(pageId);
        if (pageDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
        }
        List<AppResourceComponentDO> componentDOS = appComponentDataRepository
                .findByAppIdAndPageUuid(pageDO.getApplicationId(), pageDO.getPageUuid());
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

    public List<ComponentDTO> listComponent(String pageUuid) {
        Long applicationId = ApplicationManager.getApplicationId();
        AppResourcePageDO pageDO = pageRepository.findByAppIdAndPageUuid(applicationId, pageUuid);
        if (pageDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
        }
        List<AppResourceComponentDO> componentDOS = appComponentDataRepository
                .findByAppIdAndPageUuid(pageDO.getApplicationId(), pageDO.getPageUuid());
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

    /**
     * 根据应用ID查询当前应用下仅属于列表页（page_type = list）的组件并返回
     *
     * @param applicationId 应用ID
     * @return 仅列表页下的组件列表
     */
    public List<ComponentDTO> listComponentForListPages(Long applicationId) {
        List<AppResourceComponentDO> componentDOS = appComponentDataRepository
                .findByAppIdOnlyListPageComponents(applicationId);
        if (componentDOS == null || componentDOS.isEmpty()) {
            return Collections.emptyList();
        }
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

}
