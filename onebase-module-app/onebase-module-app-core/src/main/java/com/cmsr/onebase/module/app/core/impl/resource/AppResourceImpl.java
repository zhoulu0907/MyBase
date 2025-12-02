package com.cmsr.onebase.module.app.core.impl.resource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import jakarta.annotation.Resource;
import lombok.Setter;
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

    @Resource
    private AppPageRepository pageRepository;

    @Override
    public List<PageRespDTO> findPageListByPageSetId(Long pageSetId) {
        List<PageRespDTO> pageRespDTOs = new ArrayList<>();

        if (pageSetId == null) {
            throw new IllegalArgumentException("页面集ID不能为空");
        }
        // TODO: 后续改为使用Uuid
        pageRepository.queryChain().eq(AppResourcePageDO::getPage)
        // 读取页面集中的页面
        List<AppResourcePageDO> pageDOS = pageRepository.findAllFormPageByPageSetId(pageSetId);
        return BeanUtils.toBean(pageDOS, PageRespDTO.class);
    }
}