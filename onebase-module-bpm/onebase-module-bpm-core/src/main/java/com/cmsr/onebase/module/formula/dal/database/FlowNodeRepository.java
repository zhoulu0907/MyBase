package com.cmsr.onebase.module.formula.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowDefinitionDO;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowNodeDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Slf4j
public class FlowNodeRepository extends DataRepository<FlowNodeDO> {
    public FlowNodeRepository() {
        super(FlowNodeDO.class);
    }
    /**
     * 根据流程定义ID查询节点列表
     * @param definitionId 流程定义ID
     * @return 节点列表
     */
    public List<FlowNodeDO> queryByDefinitionId(Long definitionId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(FlowNodeDO.DEFINITION_ID, definitionId);
        return findAllByConfig(configStore)  ;
    }
}
