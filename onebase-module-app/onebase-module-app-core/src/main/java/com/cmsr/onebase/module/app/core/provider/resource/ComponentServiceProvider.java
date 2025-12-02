package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppComponentRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.ComponentDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<AppResourceComponentDO> componentDOS = appComponentDataRepository.findByAppIdAndPageUuid(pageDO.getApplicationId(), pageDO.getPageUuid());
        return BeanUtils.toBean(componentDOS, ComponentDTO.class);
    }

}
