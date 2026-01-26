package com.cmsr.onebase.module.dashboard.build.service.impl;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.dashboard.build.dal.dataobject.DashboardTemplateDO;
import com.cmsr.onebase.module.dashboard.build.mapper.DashboardProjectMapper;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProject;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectDataService;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectService;
import com.cmsr.onebase.module.dashboard.build.service.DashboardTemplateService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.dashboard.build.enums.ErrorCodeConstants.DASHBOARD_CONTENT_NOT_EXIST;

/**
 * <p>
 * 数据大屏项目服务实现类
 * 提供数据大屏项目的创建、删除等操作
 * </p>
 *
 * @author mty
 * @since 2023-04-30
 */
@Service
public class DashboardProjectServiceImpl extends ServiceImpl<DashboardProjectMapper, DashboardProject> implements DashboardProjectService {

    public static final String PROJECT_ID = "project_id";
    public static final String TENANT_ID = "tenant_id";
    public static final String APP_ID = "app_id";
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
        Long applicationId = ApplicationManager.getApplicationId();
        DashboardProject dashboardProject = new DashboardProject();
        dashboardProject.setProjectName("新大屏");
        dashboardProject.setState(NumberUtils.INTEGER_MINUS_ONE);
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

    @Override
    public Long deleteDashboardByTenantId(Long appId) {
        // 根据租户ID删除该租户下的所有数据大屏项目及其相关数据
        // 先查询该租户下的所有项目
        List<DashboardProject> dashboardProjects = list(new QueryWrapper().eq(APP_ID, appId));
        
        if (CollectionUtils.isEmpty(dashboardProjects)) {
            return 0L;
        }
        
        // 获取项目ID列表
        List<Long> projectIds = dashboardProjects.stream()
                .map(DashboardProject::getId)
                .collect(Collectors.toList());
        
        // 批量删除项目关联的数据
        dashboardProjectDataService.remove(new QueryWrapper()
                .in(PROJECT_ID, projectIds));
        // 删除项目本身
        removeByIds(projectIds);
        
        return Long.valueOf(projectIds.size());
    }
}