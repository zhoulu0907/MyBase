package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorScriptDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorScriptMapper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorScriptReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorScriptTableDef.FLOW_CONNECTOR_SCRIPT;

@Slf4j
@Repository
public class FlowConnectorScriptRepository extends BaseAppRepository<FlowConnectorScriptMapper, FlowConnectorScriptDO> {

    public PageResult<FlowConnectorScriptDO> selectConnectorScriptPage(PageConnectorScriptReqVO pageReqVO) {
        QueryWrapper query = this.query().select(FLOW_CONNECTOR_SCRIPT.ALL_COLUMNS)
                .where(FLOW_CONNECTOR_SCRIPT.CONNECTOR_UUID.eq(pageReqVO.getConnectorUuid()))
                .where(FLOW_CONNECTOR_SCRIPT.APPLICATION_ID.eq(pageReqVO.getApplicationId()))
                .where(FLOW_CONNECTOR_SCRIPT.SCRIPT_NAME.like(pageReqVO.getScriptName()).when(StringUtils.isNotBlank(pageReqVO.getScriptName())))
                .orderBy(FLOW_CONNECTOR_SCRIPT.UPDATE_TIME, false)
                .orderBy(FLOW_CONNECTOR_SCRIPT.CREATE_TIME, false);
        Page<FlowConnectorScriptDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<FlowConnectorScriptDO> pageData = this.page(page, query);
        return new PageResult<>(pageData.getRecords(), pageData.getTotalRow());
    }

    public FlowConnectorScriptDO findById(Long id) {
        return this.getMapper().selectOneById(id);
    }

}
