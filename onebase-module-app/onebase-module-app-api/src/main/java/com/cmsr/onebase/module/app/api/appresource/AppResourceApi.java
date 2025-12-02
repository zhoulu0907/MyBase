package com.cmsr.onebase.module.app.api.appresource;

import com.cmsr.onebase.module.app.api.appresource.dto.AppMenuRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.AppPagesetRespDTO;

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

    /**
     * 根据菜单UUID查询页面集
     *
     * @param menuUuid       菜单UUID
     * @param applicationId 应用ID
     * @return 页面集
     */
    AppPagesetRespDTO getPageSetByMenuUuidAndAppId(String menuUuid, Long applicationId);

    /**
     * 根据菜单ID查询菜单
     *
     * @param menuId 菜单ID
     * @return 菜单
     */
    AppMenuRespDTO getAppMenuById(Long menuId);

    /**
     * 根据菜单UUID查询菜单
     *
     * @param menuUuid       菜单UUID
     * @param applicationId 应用ID
     * @return 菜单
     */
    AppMenuRespDTO getAppMenuByUuidAndAppId(String menuUuid, Long applicationId);
}
