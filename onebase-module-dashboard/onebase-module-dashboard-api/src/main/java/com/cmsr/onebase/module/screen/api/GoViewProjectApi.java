package com.cmsr.onebase.module.screen.api;


import com.cmsr.onebase.module.screen.api.dto.GoViewProjectDTO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/27 14:29
 */
public interface GoViewProjectApi {


    Long createDashboard(GoViewProjectDTO goViewProjectDTO);

    List<GoViewProjectDTO> getDashboard(Long dashboardId);

    Long createDashboardByTemplate(Long templateId);


}
