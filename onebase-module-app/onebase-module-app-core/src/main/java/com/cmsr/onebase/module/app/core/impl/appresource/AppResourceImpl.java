package com.cmsr.onebase.module.app.core.impl.appresource;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.core.dal.database.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyang
 * @date 2025-11-14
 */
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

        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("pageset_id", pageSetId);

        // 读取页面集中的页面
        List<AppResourcePageDO> pageDOS = pageRepository.list();

        pageDOS.forEach(pageDO -> {
            PageRespDTO pageRespDTO = BeanUtils.toBean(pageDO, PageRespDTO.class);
            pageRespDTOs.add(pageRespDTO);
        });

        return pageRespDTOs;
    }
}
