package com.cmsr.onebase.module.app.api.appresource;

import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;

import java.util.List;

/**
 * @author liyang
 * @date 2025-11-14
 */
public interface AppResourceApi {

    /**
     * 根据页面集ID查询页面
     *
     * @param pageSetId 页面集ID
     * @return 页面列表
     */
    List<PageRespDTO> findPageListByPageSetId(Long pageSetId);
}
