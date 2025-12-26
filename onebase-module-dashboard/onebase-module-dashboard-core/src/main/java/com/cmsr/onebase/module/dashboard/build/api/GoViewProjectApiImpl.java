package com.cmsr.onebase.module.dashboard.build.api;

import cn.hutool.core.bean.BeanUtil;
import com.cmsr.onebase.module.dashboard.build.dal.dataobject.DashboardTemplateDO;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProject;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.cmsr.onebase.module.dashboard.build.service.IDashboardTemplateService;
import com.cmsr.onebase.module.dashboard.build.service.IGoviewProjectDataService;
import com.cmsr.onebase.module.dashboard.build.service.IGoviewProjectService;
import com.cmsr.onebase.module.screen.api.GoViewProjectApi;
import com.cmsr.onebase.module.screen.api.dto.GoViewProjectDTO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class GoViewProjectApiImpl implements GoViewProjectApi {

    @Resource
    private IGoviewProjectService iGoviewProjectService;

    @Resource
    private IGoviewProjectDataService iGoviewProjectDataService;

    @Resource
    private IDashboardTemplateService dashboardTemplateService;


    @Override
    public Long createDashboard(GoViewProjectDTO goViewProjectDTO) {
        DashboardProject dashboardProject = BeanUtil.toBean(goViewProjectDTO, DashboardProject.class);
        iGoviewProjectService.save(dashboardProject);
        return dashboardProject.getId();
    }

    @Override
    public List<GoViewProjectDTO> getDashboard(Long dashboardId) {
        DashboardProject dashboardProject = iGoviewProjectService.getById(dashboardId);
        return List.of(BeanUtil.toBean(dashboardProject, GoViewProjectDTO.class));
    }

    @Override
    public Long createDashboardByTemplate(Long templateId) {
        // 查询模板信息，创建数据大屏
        DashboardTemplateDO dashboardTemplate = dashboardTemplateService.getDashboardTemplate(templateId);
        if (dashboardTemplate == null){
            return null;
        }
        Long applicationId = dashboardTemplate.getAppId();

        DashboardProject dashboardProject = new DashboardProject();
        dashboardProject.setProjectName("新大屏");
        dashboardProject.setState(-1);
        dashboardProject.setAppId(applicationId);
        dashboardProject.setIndexImage(dashboardTemplate.getIndexImage());
        iGoviewProjectService.save(dashboardProject);
        DashboardProjectData dashboardProjectData = new DashboardProjectData();
        dashboardProjectData.setProjectId(dashboardProject.getId());
        dashboardProjectData.setContent(dashboardTemplate.getContent());
        dashboardProjectData.setAppId(applicationId);
        iGoviewProjectDataService.save(dashboardProjectData);
        return dashboardProject.getId();
    }
}
