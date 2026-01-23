package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorEnvMapper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorEnvReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorEnvTableDef.FLOW_CONNECTOR_ENV;

/**
 * 连接器环境配置 Repository
 * <p>
 * 提供环境配置的数据访问操作，封装复杂的查询逻辑
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Slf4j
@Repository
public class FlowConnectorEnvRepository extends BaseAppRepository<FlowConnectorEnvMapper, FlowConnectorEnvDO> {

    /**
     * 分页查询环境配置
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<FlowConnectorEnvDO> selectPage(PageConnectorEnvReqVO pageReqVO) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_ENV.TYPE_CODE.eq(pageReqVO.getTypeCode())
                        .when(StringUtils.isNotBlank(pageReqVO.getTypeCode())))
                .where(FLOW_CONNECTOR_ENV.ENV_CODE.eq(pageReqVO.getEnvCode())
                        .when(StringUtils.isNotBlank(pageReqVO.getEnvCode())))
                .where(FLOW_CONNECTOR_ENV.ENV_NAME.like(pageReqVO.getEnvName())
                        .when(StringUtils.isNotBlank(pageReqVO.getEnvName())))
                .where(FLOW_CONNECTOR_ENV.ACTIVE_STATUS.eq(pageReqVO.getActiveStatus())
                        .when(pageReqVO.getActiveStatus() != null))
                .orderBy(FLOW_CONNECTOR_ENV.SORT_ORDER, true)
                .orderBy(FLOW_CONNECTOR_ENV.CREATE_TIME, false);

        Page<FlowConnectorEnvDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<FlowConnectorEnvDO> pageData = this.page(page, query);

        return new PageResult<>(pageData.getRecords(), pageData.getTotalRow());
    }

    /**
     * 根据环境UUID查询
     *
     * @param envUuid 环境配置UUID
     * @return 环境配置DO
     */
    public FlowConnectorEnvDO selectByEnvUuid(String envUuid) {
        return getMapper().selectByEnvUuid(envUuid);
    }

    /**
     * 根据连接器类型查询环境配置列表
     *
     * @param typeCode 连接器类型编号
     * @return 环境配置列表
     */
    public List<FlowConnectorEnvDO> selectByTypeCode(String typeCode) {
        return getMapper().selectByTypeCode(typeCode);
    }

    /**
     * 检查环境编码是否已存在
     *
     * @param typeCode      连接器类型编号
     * @param envCode       环境编码
     * @param applicationId 应用ID
     * @return 是否存在
     */
    public boolean existsByTypeAndEnvCode(String typeCode, String envCode, Long applicationId) {
        return getMapper().countByTypeAndEnvCode(typeCode, envCode, applicationId) > 0;
    }
}
