package com.cmsr.onebase.module.system.service.project;

import com.cmsr.onebase.module.system.dal.dataobject.project.ProjectAppRelationDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * 项目应用关联 Service 接口
 *
 * @author system
 * @date 2026-04-22
 */
public interface ProjectAppRelationService {

    /**
     * 创建项目应用关联
     *
     * @param projectId     项目ID
     * @param applicationId 应用ID
     * @return 编号
     */
    Long createProjectAppRelation(Long projectId, Long applicationId);

    /**
     * 删除项目应用关联
     *
     * @param applicationId 应用ID
     */
    void removeProjectAppRelation(Long applicationId);

    /**
     * 根据项目ID查询所有应用ID列表
     *
     * @param projectId 项目ID
     * @return 应用ID列表
     */
    List<Long> listApplicationIdsByProjectId(Long projectId);

}
