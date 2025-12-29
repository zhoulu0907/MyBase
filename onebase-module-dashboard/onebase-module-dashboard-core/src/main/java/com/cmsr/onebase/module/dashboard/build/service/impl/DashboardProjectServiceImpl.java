package com.cmsr.onebase.module.dashboard.build.service.impl;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.dashboard.build.dal.dataobject.DashboardTemplateDO;
import com.cmsr.onebase.module.dashboard.build.mapper.DashboardProjectMapper;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProject;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectDataService;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectService;
import com.cmsr.onebase.module.dashboard.build.service.DashboardTemplateService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.dashboard.build.enums.ErrorCodeConstants.DASHBOARD_CONTENT_NOT_EXIST;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mty
 * @since 2023-04-30
 */
@Service
public class DashboardProjectServiceImpl extends ServiceImpl<DashboardProjectMapper, DashboardProject> implements DashboardProjectService {

    @Resource
    DashboardProjectDataService dashboardProjectDataService;

    @Resource
    DashboardTemplateService dashboardTemplateService;

    @Override
    public Long createDashboardByTemplate(Long templateId) {
        // 查询模板信息，创建数据大屏
        DashboardTemplateDO dashboardTemplate = dashboardTemplateService.getDashboardTemplate(templateId);
        if (dashboardTemplate == null){
            throw exception(DASHBOARD_CONTENT_NOT_EXIST);
        }
        Long applicationId = ApplicationManager.getApplicationId();;

        DashboardProject dashboardProject = new DashboardProject();
        dashboardProject.setProjectName("新大屏");
        dashboardProject.setState(-1);
        dashboardProject.setAppId(applicationId);
        dashboardProject.setIndexImage(dashboardTemplate.getIndexImage());
        save(dashboardProject);
        DashboardProjectData dashboardProjectData = new DashboardProjectData();
        dashboardProjectData.setProjectId(dashboardProject.getId());
        dashboardProjectData.setContent(dashboardTemplate.getContent());
        dashboardProjectData.setAppId(applicationId);
        dashboardProjectDataService.save(dashboardProjectData);

        return dashboardProject.getId();
    }

}