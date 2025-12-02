package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.PageRespDTO;
import com.cmsr.onebase.module.app.core.enums.appresource.AppResourceErrorCodeConstants;
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


    public PageRespDTO getPage(Long pageId) {
        AppResourcePageDO pageDO = pageRepository.getById(pageId);
        if (pageDO == null) {
            throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_NOT_EXIST);
        }
        return BeanUtils.toBean(pageDO, PageRespDTO.class);
    }

    public List<PageDTO> getFormPageListByAppId(Long applicationId) {
        List<String> menuUuidList = menuRepository.findMenuUuidListByApplication(applicationId);
        if (CollectionUtils.isEmpty(menuUuidList)) {
            return Collections.emptyList();
        }
        List<String> pageSetUuidList = pageSetRepository.findPageSetUuidListByMenuUuids(menuUuidList);
        if (CollectionUtils.isEmpty(pageSetUuidList)) {
            return Collections.emptyList();
        }
        List<AppResourcePageDO> pageDOList = pageRepository.findAllFormPageByPageSetUuids(pageSetUuidList);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);
        return pageDTOList;
    }

    public String getMetadataByPageUuid(String pageUuid) {
        String pageSetUuid = pageRepository.getPageSetUuidByPageUuid(pageUuid);
        String mainMetadata = pageSetRepository.getMainMetadataByUuid(pageSetUuid);

        return mainMetadata;
    }


    public List<PageDTO> listPageView(String pageSetUuid) {
        List<AppResourcePageDO> pageDOList = pageRepository.findAllFormPageByPageSetUuid(pageSetUuid);
        List<PageDTO> pageDTOList = BeanUtils.toBean(pageDOList, PageDTO.class);
        return pageDTOList;
    }


}
