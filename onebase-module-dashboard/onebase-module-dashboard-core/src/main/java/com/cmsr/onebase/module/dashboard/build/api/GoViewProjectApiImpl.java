package com.cmsr.onebase.module.dashboard.build.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.module.dashboard.build.dal.dataobject.DashboardTemplateDO;
import com.cmsr.onebase.module.dashboard.build.model.GoviewProject;
import com.cmsr.onebase.module.dashboard.build.model.GoviewProjectData;
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
    public String createDashboard(GoViewProjectDTO goViewProjectDTO) {
        GoviewProject goviewProject = BeanUtil.toBean(goViewProjectDTO, GoviewProject.class);
        iGoviewProjectService.save(goviewProject);
        return goviewProject.getId();
    }

    @Override
    public List<GoViewProjectDTO> getDashboard(String dashboardId) {
        GoviewProject goviewProject = iGoviewProjectService.getById(dashboardId);
        return List.of(BeanUtil.toBean(goviewProject, GoViewProjectDTO.class));
    }

    @Override
    public String createDashboardByTemplate(String templateId) {
        // todo: 查询模板信息，创建数据大屏
        DashboardTemplateDO dashboardTemplate = dashboardTemplateService.getDashboardTemplate(Long.parseLong(templateId));
        if (dashboardTemplate == null){
            return null;
        }
        Long applicationId = dashboardTemplate.getAppId();
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        GoviewProject goviewProject = new GoviewProject();
        goviewProject.setProjectName("新大屏");
        goviewProject.setState(-1);
        goviewProject.setAppId(applicationId);
        if (loginUser != null){
            goviewProject.setCreateUserId(loginUser.getId().toString());
        }
        iGoviewProjectService.save(goviewProject);
        GoviewProjectData goviewProjectData = new GoviewProjectData();
        goviewProjectData.setProjectId(goviewProject.getId());
        goviewProjectData.setContent(dashboardTemplate.getContent());
        goviewProjectData.setCreateTime(DateUtil.now());
        if (loginUser != null){
            goviewProjectData.setCreateUserId(loginUser.getId().toString());
        }
        iGoviewProjectDataService.save(goviewProjectData);
        return goviewProject.getId();
    }
}
