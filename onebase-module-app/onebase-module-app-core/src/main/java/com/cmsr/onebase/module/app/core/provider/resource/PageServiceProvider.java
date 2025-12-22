package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppWorkbenchPageRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceWorkbenchPageDO;
import com.cmsr.onebase.module.app.core.dto.resource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.resource.PageRespDTO;
import com.cmsr.onebase.module.app.core.enums.resource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.resource.PageTypeSetEnum;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Setter
@Service
public class PageServiceProvider {

    @Autowired
    private AppPageRepository pageRepository;

    @Autowired
    private AppPageSetRepository pageSetRepository;

    @Autowired
    private AppMenuRepository menuRepository;

    @Autowired
    private AppWorkbenchPageRepository workbenchPageRepository;


    public PageRespDTO getPage(Long pageId) {
        AppResourcePageDO pageDO = pageRepository.getById(pageId);
        if (pageDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
        }
        return BeanUtils.toBean(pageDO, PageRespDTO.class);
    }

    public List<PageDTO> getPageListByAppId(Long applicationId) {
        List<String> menuUuidList = menuRepository.findMenuUuidListByApplication(applicationId);
        if (CollectionUtils.isEmpty(menuUuidList)) {
            return Collections.emptyList();
        }
        List<String> pageSetUuidList = pageSetRepository.findPageSetUuidListByMenuUuids(applicationId, menuUuidList);
        if (CollectionUtils.isEmpty(pageSetUuidList)) {
            return Collections.emptyList();
        }
        List<AppResourcePageDO> pageDOList = pageRepository.findAllPageByPageSetUuids(applicationId, pageSetUuidList);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);
        return pageDTOList;
    }

    public String getMetadataByPageId(Long pageId) {
        AppResourcePageDO pageDO = pageRepository.getById(pageId);
        Long applicationId = pageDO.getApplicationId();
        String pageSetUuid = pageDO.getPageSetUuid();
        String mainMetadata = pageSetRepository.getMainMetadataByAppIdAndUuid(applicationId, pageSetUuid);
        return mainMetadata;
    }

    public String getMetadataByPageUuid(String pageUuid) {
        Long applicationId = ApplicationManager.getApplicationId();
        AppResourcePageDO pageDO = pageRepository.findByAppIdAndPageUuid(applicationId, pageUuid);
        String pageSetUuid = pageDO.getPageSetUuid();
        String mainMetadata = pageSetRepository.getMainMetadataByAppIdAndUuid(applicationId, pageSetUuid);
        return mainMetadata;
    }

    public List<PageDTO> listPageView(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = pageSetRepository.getById(pageSetId);
        Long applicationId = pageSetDO.getApplicationId();
        String pageSetUuid = pageSetDO.getPageSetUuid();
        Integer pageSetType = pageSetDO.getPageSetType();

        // 根据页面集类型查询不同的表
        if (PageTypeSetEnum.isWorkBenchType(pageSetType)) {
            // 工作台类型，查询工作台页面表
            List<AppResourceWorkbenchPageDO> workbenchPageDOList = workbenchPageRepository.findByPageSetUuid(applicationId, pageSetUuid);
            return BeanUtils.toBean(workbenchPageDOList, PageDTO.class);
        } else {
            // 普通表单或流程表单类型，查询普通页面表
            List<AppResourcePageDO> pageDOList = pageRepository.findAllFormPageByAppIdAndPageSetUuid(applicationId, pageSetUuid);
            return BeanUtils.toBean(pageDOList, PageDTO.class);
        }
    }


}
