package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.project.ProjectAppRelationDO;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemProjectAppRelationMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 项目应用关联数据访问层
 *
 * @author system
 * @date 2026-04-22
 */
@Repository
public class ProjectAppRelationDataRepository extends BaseDataRepository<SystemProjectAppRelationMapper, ProjectAppRelationDO> {

    /**
     * 根据应用ID删除关联
     *
     * @param applicationId 应用ID
     */
    public void deleteByApplicationId(Long applicationId) {
        if (applicationId == null) {
            return;
        }
        remove(query().eq(ProjectAppRelationDO.APPLICATION_ID, applicationId));
    }
}
