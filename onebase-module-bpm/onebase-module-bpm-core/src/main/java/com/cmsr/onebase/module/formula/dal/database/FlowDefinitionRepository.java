package com.cmsr.onebase.module.formula.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.formula.dal.dataobject.FlowDefinitionDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Slf4j
public class FlowDefinitionRepository extends DataRepository<FlowDefinitionDO> {
    public FlowDefinitionRepository() {
        super(FlowDefinitionDO.class);
    }
    public List<FlowDefinitionDO> queryByCodeList(List<String> flowCodeList) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowDefinitionDO.FLOW_CODE, flowCodeList);
        return findAllByConfig(configStore)  ;
    }

    public List<FlowDefinitionDO> queryByFormId(Long formId){
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(FlowDefinitionDO.FORM_PATH, String.valueOf(formId));
        configStore.and(FlowDefinitionDO.FORM_CUSTOM, "Y");
        return findAllByConfig(configStore);
    }
}
