package com.cmsr.onebase.module.app.core.provider.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.app.core.enums.resource.AppResourceErrorCodeConstants;
import com.cmsr.onebase.module.screen.api.DashboardApi;
import com.cmsr.onebase.module.screen.api.dto.GoViewProjectDTO;
import com.cmsr.onebase.module.screen.api.enums.DashboardCreateTypeSetEnum;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Setter
@Service
public class DashboardServiceProvider {

    @Resource
    private DashboardApi dashboardApi;

    public Long createDashboard(String createType, Long dashboardId) {
        //根据数据大屏的创建类型创建数据大屏页面
        if (Objects.equals(createType, DashboardCreateTypeSetEnum.DASHBOARD_LINK.getCode())){
            //1.1 如果是绑定现有大屏，则查询数据大屏信息，如不存在则报错
            List<GoViewProjectDTO> dashboardList = dashboardApi.getDashboard(dashboardId);
            if (dashboardList.isEmpty()){
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.DASHBOARD_NOT_EXIST);
            }
        } else if (Objects.equals(createType, DashboardCreateTypeSetEnum.DASHBOARD_TEMPLATE.getCode())){
            //1.2 从模板创建数据大屏
            Long newDashboardId = dashboardApi.createDashboardByTemplate(dashboardId);
            if (dashboardId == null){
                throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.DASHBOARD_TEMPLATE_NOT_EXIST);
            }
            return newDashboardId;
        } else {
            //1.3 从空白页创建数据大屏
            GoViewProjectDTO goViewProjectDTO = new GoViewProjectDTO();

            goViewProjectDTO.setProjectName("新大屏");
            goViewProjectDTO.setState(-1);
            goViewProjectDTO.setAppId(ApplicationManager.getApplicationId());
            return dashboardApi.createDashboard(goViewProjectDTO);

        }
        return null;
    }

    public List<GoViewProjectDTO> getDashboard(Long dashboardId){
        return dashboardApi.getDashboard(dashboardId);
    }
}
