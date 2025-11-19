package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class FlowConnectorRepository extends DataRepository<FlowConnectorDO> {
    public FlowConnectorRepository() {
        super(FlowConnectorDO.class);
    }

    public PageResult<FlowConnectorDO> getConnectorPage(PageConnectorReqVO pageReqVO) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("application_id", pageReqVO.getApplicationId());
        String connectorName = pageReqVO.getConnectorName();
        if (StringUtils.isNotBlank(connectorName)) {
            cs.like("connector_name", connectorName);
        }
        String level1Code = pageReqVO.getLevel1Code();
        if (StringUtils.isNotBlank(level1Code)) {
            cs.eq("level1_code", level1Code);
        }
        String level2Code = pageReqVO.getLevel2Code();
        if (StringUtils.isNotBlank(level2Code)) {
            cs.eq("level2_code", level2Code);
        }
        String level3Code = pageReqVO.getLevel3Code();
        if (StringUtils.isNotBlank(level3Code)) {
            cs.eq("level3_code", level3Code);
        }
        cs.order("update_time", Order.TYPE.DESC);
        cs.order("create_time", Order.TYPE.DESC);

        return findPageWithConditions(cs, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
