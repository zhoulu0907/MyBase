package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowTableDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLWorkflowTableMapper;
import com.cmsr.onebase.module.etl.core.enums.ETLConstants;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ETLWorkflowTableRepository extends BaseAppRepository<ETLWorkflowTableMapper, ETLWorkflowTableDO> {

    public void deleteByWorkflowId(Long workflowId) {
        QueryWrapper queryWrapper = query().eq(ETLWorkflowTableDO::getWorkflowId, workflowId);
        remove(queryWrapper);
    }

    public boolean existsByDatasourceId(Long datasourceId) {
        QueryWrapper queryWrapper = query().eq(ETLWorkflowTableDO::getDatasourceId, datasourceId);
        return exists(queryWrapper);
    }

    public Set<Long> findSourceTableIdsByWorkflowId(Long workflowId) {
        QueryWrapper queryWrapper = query().select(ETLWorkflowTableDO::getTableId)
                .eq(ETLWorkflowTableDO::getWorkflowId, workflowId)
                .eq(ETLWorkflowTableDO::getRelation, ETLConstants.WORKFLOW_TABLE_RELATION_SOURCE);
        List<ETLWorkflowTableDO> relations = list(queryWrapper);
        return relations.stream()
                .map(ETLWorkflowTableDO::getTableId)
                .collect(Collectors.toSet());
    }

    public ETLWorkflowTableDO findTargetTableIdByWorkflowId(Long workflowId) {
        QueryWrapper queryWrapper = query().select(ETLWorkflowTableDO::getTableId)
                .eq(ETLWorkflowTableDO::getWorkflowId, workflowId)
                .eq(ETLWorkflowTableDO::getRelation, ETLConstants.WORKFLOW_TABLE_RELATION_TARGET);
        return getOne(queryWrapper);
    }
}
