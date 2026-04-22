package com.cmsr.onebase.module.system.api.project;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.service.project.ProjectAppRelationService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class ProjectAppRelationApiImpl implements ProjectAppRelationApi {

    @Resource
    private ProjectAppRelationService projectAppRelationService;

    @Override
    public CommonResult<Long> createProjectAppRelation(Long projectId, Long applicationId) {
        Long id = projectAppRelationService.createProjectAppRelation(projectId, applicationId);
        return success(id);
    }

    @Override
    public CommonResult<Boolean> removeProjectAppRelation(Long applicationId) {
        projectAppRelationService.removeProjectAppRelation(applicationId);
        return success(true);
    }

    @Override
    public CommonResult<List<Long>> listApplicationIdsByProjectId(Long projectId) {
        List<Long> applicationIds = projectAppRelationService.listApplicationIdsByProjectId(projectId);
        return success(applicationIds);
    }

}
