package com.cmsr.onebase.module.system.service.project;

import com.cmsr.onebase.module.system.dal.dataobject.project.ProjectAppRelationDO;
import com.cmsr.onebase.module.system.dal.database.ProjectAppRelationDataRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目应用关联 Service 实现类
 *
 * @author system
 * @date 2026-04-22
 */
@Service
@Validated
public class ProjectAppRelationServiceImpl implements ProjectAppRelationService {

    @Resource
    private ProjectAppRelationDataRepository projectAppRelationDataRepository;

    @Override
    public Long createProjectAppRelation(String projectId, Long applicationId) {
        // 插入
        ProjectAppRelationDO projectAppRelationDO = new ProjectAppRelationDO();
        projectAppRelationDO.setProjectId(projectId);
        projectAppRelationDO.setApplicationId(applicationId);
        projectAppRelationDataRepository.insert(projectAppRelationDO);
        return projectAppRelationDO.getId();
    }

    @Override
    public void removeProjectAppRelation(Long applicationId) {
        // 删除
        projectAppRelationDataRepository.remove(
            projectAppRelationDataRepository.query()
                .eq(ProjectAppRelationDO.APPLICATION_ID, applicationId)
        );
    }

    @Override
    public List<Long> listApplicationIdsByProjectId(String projectId) {
        // 查询
        List<ProjectAppRelationDO> relations = projectAppRelationDataRepository.list(
            projectAppRelationDataRepository.query()
                .eq(ProjectAppRelationDO.PROJECT_ID, projectId)
        );
        // 提取应用ID列表
        return relations.stream()
            .map(ProjectAppRelationDO::getApplicationId)
            .collect(Collectors.toList());
    }

}
