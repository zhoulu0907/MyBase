package com.cmsr.onebase.module.flow.core.dal.database.connector;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.dal.dataobject.connector.FlowConnectorScriptDO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorScriptReqVO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class FlowConnectorScriptRepository extends DataRepository<FlowConnectorScriptDO> {
    public FlowConnectorScriptRepository() {
        super(FlowConnectorScriptDO.class);
    }

    public PageResult<FlowConnectorScriptDO> getConnectorScriptPage(PageConnectorScriptReqVO pageReqVO) {
        ConfigStore cs = new DefaultConfigStore();
        cs.columns("id", "connector_id", "script_name", "script_type", "description", "create_time", "update_time");
        cs.eq("connector_id", pageReqVO.getConnectorId());
        String scriptName = pageReqVO.getScriptName();
        if (StringUtils.isNotBlank(scriptName)) {
            cs.eq("script_name", scriptName);
        }
        cs.order("update_time", Order.TYPE.DESC);
        cs.order("create_time", Order.TYPE.DESC);

        return findPageWithConditions(cs, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
