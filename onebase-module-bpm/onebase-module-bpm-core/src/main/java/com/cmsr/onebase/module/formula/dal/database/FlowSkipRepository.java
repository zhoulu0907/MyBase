package com.cmsr.onebase.module.formula.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowNodeDO;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowSkipDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class FlowSkipRepository extends DataRepository<FlowSkipDO> {
    public FlowSkipRepository() {
        super(FlowSkipDO.class);
    }
    /**
     * 根据流程定义ID查询跳转关系
     * @param definitionId 流程定义ID
     * @return 跳转关系列表
     */
    public List<FlowSkipDO> queryByDefinitionId(Long definitionId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(FlowSkipDO.DEFINITION_ID, definitionId);
        return findAllByConfig(configStore)  ;
    }
}
