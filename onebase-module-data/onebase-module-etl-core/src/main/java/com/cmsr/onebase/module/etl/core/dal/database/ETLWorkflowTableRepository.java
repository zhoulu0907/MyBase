package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowTableDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLWorkflowTableMapper;
import com.cmsr.onebase.module.etl.core.enums.ETLConstants;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Repository
public class ETLWorkflowTableRepository extends BaseAppRepository<ETLWorkflowTableMapper, ETLWorkflowTableDO> {

    public void deleteByWorkflow(String workflowUuid) {
        this.updateChain()
                .eq(ETLWorkflowTableDO::getWorkflowUuid, workflowUuid)
                .remove();
    }

    public boolean existsByDatasource(String datasourceUuid) {
        QueryWrapper queryWrapper = query()
                .eq(ETLWorkflowTableDO::getDatasourceUuid, datasourceUuid);
        return exists(queryWrapper);
    }

    public Set<String> findSourceTablesByWorkflow(String workflowUuid) {
        QueryWrapper queryWrapper = query().select(ETLWorkflowTableDO::getTableUuid)
                .eq(ETLWorkflowTableDO::getWorkflowUuid, workflowUuid)
                .eq(ETLWorkflowTableDO::getRelation, ETLConstants.WORKFLOW_TABLE_RELATION_SOURCE);
        return new HashSet<>(objListAs(queryWrapper, String.class));
    }

    public String findTargetTableByWorkflow(String workflowUuid) {
        QueryWrapper queryWrapper = query().select(ETLWorkflowTableDO::getTableUuid)
                .eq(ETLWorkflowTableDO::getWorkflowUuid, workflowUuid)
                .eq(ETLWorkflowTableDO::getRelation, ETLConstants.WORKFLOW_TABLE_RELATION_TARGET);
        return getObjAs(queryWrapper, String.class);
    }
}
