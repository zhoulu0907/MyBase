package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowTableDO;
import com.cmsr.onebase.module.etl.core.enums.ETLConstants;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ETLWorkflowTableRepository extends DataRepository<ETLWorkflowTableDO> {
    public ETLWorkflowTableRepository() {
        super(ETLWorkflowTableDO.class);
    }


    public void deleteByWorkflowId(Long workflowId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("workflow_id", workflowId);
        deleteByConfig(cs);
    }

    public boolean existsByDatasourceId(Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);
        ETLWorkflowTableDO existsAtLeastOne = findOne(cs);
        return existsAtLeastOne != null;
    }

    public Set<Long> findSourceTableIdsByWorkflowId(Long workflowId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("workflow_id", workflowId);
        cs.eq("relation", ETLConstants.WORKFLOW_TABLE_RELATION_SOURCE);
        List<ETLWorkflowTableDO> relations = findAllByConfig(cs);
        return relations.stream()
                .map(ETLWorkflowTableDO::getTableId)
                .collect(Collectors.toSet());
    }

    public ETLWorkflowTableDO findTargetTableIdByWorkflowId(Long workflowId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("workflow_id", workflowId);
        cs.eq("relation", ETLConstants.WORKFLOW_TABLE_RELATION_TARGET);
        return findOne(cs);
    }
}
