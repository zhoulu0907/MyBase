package com.cmsr.onebase.module.dashboard.build.api;

import cn.hutool.core.bean.BeanUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.dashboard.build.dal.dataobject.DashboardTemplateDO;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProject;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.cmsr.onebase.module.dashboard.build.service.DashboardTemplateService;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectDataService;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectService;
import com.cmsr.onebase.module.screen.api.DashboardProjectApi;
import com.cmsr.onebase.module.screen.api.dto.DashboardProjectDTO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class DashboardProjectApiImpl implements DashboardProjectApi {

    @Resource
    private DashboardProjectService dashboardProjectService;

    @Resource
    private DashboardProjectDataService dashboardProjectDataService;

    @Resource
    private DashboardTemplateService dashboardTemplateService;


    @Override
    public Long createDashboard(DashboardProjectDTO dashboardProjectDTO) {
        DashboardProject dashboardProject = BeanUtil.toBean(dashboardProjectDTO, DashboardProject.class);
        dashboardProjectService.save(dashboardProject);
        return dashboardProject.getId();
    }

    @Override
    public List<DashboardProjectDTO> getDashboard(Long dashboardId) {
        DashboardProject dashboardProject = dashboardProjectService.getById(dashboardId);
        return List.of(BeanUtil.toBean(dashboardProject, DashboardProjectDTO.class));
    }

    @Override
    public Long createDashboardByTemplate(Long templateId, String dashboardName) {
        // 查询模板信息，创建数据大屏
        DashboardTemplateDO dashboardTemplate = dashboardTemplateService.getDashboardTemplate(templateId);
        if (dashboardTemplate == null){
            return null;
        }
        Long applicationId = ApplicationManager.getApplicationId();

        DashboardProject dashboardProject = new DashboardProject();
        dashboardProject.setProjectName(dashboardName);
        dashboardProject.setState(NumberUtils.INTEGER_MINUS_ONE);
        dashboardProject.setAppId(applicationId);
        dashboardProject.setIndexImage(dashboardTemplate.getIndexImage());
        dashboardProjectService.save(dashboardProject);
        DashboardProjectData dashboardProjectData = new DashboardProjectData();
        dashboardProjectData.setProjectId(dashboardProject.getId());
        dashboardProjectData.setContent(dashboardTemplate.getContent());
        dashboardProjectData.setAppId(applicationId);
        dashboardProjectDataService.save(dashboardProjectData);
        return dashboardProject.getId();
    }
}
