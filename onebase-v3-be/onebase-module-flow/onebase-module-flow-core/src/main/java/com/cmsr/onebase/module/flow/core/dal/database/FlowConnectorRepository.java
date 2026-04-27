package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorMapper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorTableDef.FLOW_CONNECTOR;

@Slf4j
@Repository
public class FlowConnectorRepository extends BaseAppRepository<FlowConnectorMapper, FlowConnectorDO> {

    public PageResult<FlowConnectorDO> selectConnectorPage(PageConnectorReqVO pageReqVO) {
        String connectorName = pageReqVO.getConnectorName();

        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR.APPLICATION_ID.eq(pageReqVO.getApplicationId()))
                .where(FLOW_CONNECTOR.CONNECTOR_NAME.like(connectorName).when(StringUtils.isNotBlank(connectorName)))
                .where(FLOW_CONNECTOR.TYPE_CODE.eq(pageReqVO.getTypeCode())
                        .when(StringUtils.isNotBlank(pageReqVO.getTypeCode())))
                .where(FLOW_CONNECTOR.ACTIVE_STATUS.eq(pageReqVO.getActiveStatus())
                        .when(pageReqVO.getActiveStatus() != null))
                .orderBy(FLOW_CONNECTOR.UPDATE_TIME, false)
                .orderBy(FLOW_CONNECTOR.CREATE_TIME, false);
        Page<FlowConnectorDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<FlowConnectorDO> pageData = this.page(page, query);

        return new PageResult<>(pageData.getRecords(), pageData.getTotalRow());
    }

    /**
     * List connector instances by type code
     * Uses withoutApplicationCondition to disable auto-filtering and manually set
     * applicationId
     */
    public java.util.List<FlowConnectorDO> listByType(String typeCode) {
        QueryWrapper queryWrapper = this.query()
                .where(FLOW_CONNECTOR.TYPE_CODE.eq(typeCode))
                .orderBy(FLOW_CONNECTOR.CREATE_TIME, false);
        return this.list(queryWrapper);
    }

    /**
     * Count connector instances by type codes (only non-deleted records)
     *
     * @param typeCodes the connector type code list
     * @return Map<typeCode, count>
     */
    public Map<String, Integer> countByTypeCodes(List<String> typeCodes) {
        if (typeCodes == null || typeCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Map<String, Object>> result = this.getMapper().countByTypeCodes(typeCodes);
        if (result == null || result.isEmpty()) {
            return Collections.emptyMap();
        }

        return result.stream()
                .collect(Collectors.toMap(
                        record -> (String) record.get("type_code"),
                        record -> ((Number) record.get("count")).intValue()));
    }
}
