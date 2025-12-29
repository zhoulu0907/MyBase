package com.cmsr.onebase.module.screen.api;


import com.cmsr.onebase.module.screen.api.dto.DashboardProjectDTO;

import java.util.List;

/**
 * @Date：2025/10/27 14:29
 */
public interface DashboardProjectApi {


    Long createDashboard(DashboardProjectDTO dashboardProjectDTO);

    List<DashboardProjectDTO> getDashboard(Long dashboardId);

    Long createDashboardByTemplate(Long templateId, String dashboardName);


}
