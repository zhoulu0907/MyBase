package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlWorkflowTableDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlWorkflowTableMapper;
import com.cmsr.onebase.module.etl.core.enums.EtlConstants;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Repository
public class EtlWorkflowTableRepository extends BaseAppRepository<EtlWorkflowTableMapper, EtlWorkflowTableDO> {

    public void deleteByWorkflow(String workflowUuid) {
        this.updateChain()
                .eq(EtlWorkflowTableDO::getWorkflowUuid, workflowUuid)
                .remove();
    }

    public boolean existsByDatasource(String datasourceUuid) {
        QueryWrapper queryWrapper = query()
                .eq(EtlWorkflowTableDO::getDatasourceUuid, datasourceUuid);
        return exists(queryWrapper);
    }

    public Set<String> findSourceTablesByWorkflow(String workflowUuid) {
        QueryWrapper queryWrapper = query().select(EtlWorkflowTableDO::getTableUuid)
                .eq(EtlWorkflowTableDO::getWorkflowUuid, workflowUuid)
                .eq(EtlWorkflowTableDO::getRelation, EtlConstants.WORKFLOW_TABLE_RELATION_SOURCE);
        return new HashSet<>(objListAs(queryWrapper, String.class));
    }

    public String findTargetTableByWorkflow(String workflowUuid) {
        QueryWrapper queryWrapper = query().select(EtlWorkflowTableDO::getTableUuid)
                .eq(EtlWorkflowTableDO::getWorkflowUuid, workflowUuid)
                .eq(EtlWorkflowTableDO::getRelation, EtlConstants.WORKFLOW_TABLE_RELATION_TARGET);
        return getObjAs(queryWrapper, String.class);
    }
}
