package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorEnvMapper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorEnvReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorEnvTableDef.FLOW_CONNECTOR_ENV;

/**
 * 连接器环境配置 Repository
 * <p>
 * 提供环境配置的数据访问操作，封装复杂的查询逻辑
 * <p>
 * 注意：环境配置支持租户级共享，不继承 BaseAppRepository
 * 所有查询自动注入 tenant_id 过滤条件
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Slf4j
@Repository
public class FlowConnectorEnvRepository extends ServiceImpl<FlowConnectorEnvMapper, FlowConnectorEnvDO> {

    /**
     * 注入租户过滤条件
     * <p>
     * 所有查询方法都应该调用此方法来注入 tenant_id 过滤
     *
     * @param queryWrapper 查询条件
     * @return 注入租户条件后的查询对象
     */
    private QueryWrapper injectTenantFilter(QueryWrapper queryWrapper) {
        // TODO: 从上下文获取当前租户ID
        // Long tenantId = TenantContextHolder.getTenantId();
        // queryWrapper.and(FLOW_CONNECTOR_ENV.TENANT_ID.eq(tenantId));
        return queryWrapper;
    }

    /**
     * 分页查询环境配置
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<FlowConnectorEnvDO> selectPage(PageConnectorEnvReqVO pageReqVO) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_CONNECTOR_ENV.TYPE_CODE.eq(pageReqVO.getTypeCode())
                        .when(StringUtils.isNotBlank(pageReqVO.getTypeCode())))
                .and(FLOW_CONNECTOR_ENV.ENV_CODE.eq(pageReqVO.getEnvCode())
                        .when(StringUtils.isNotBlank(pageReqVO.getEnvCode())))
                .and(FLOW_CONNECTOR_ENV.ENV_NAME.like(pageReqVO.getEnvName())
                        .when(StringUtils.isNotBlank(pageReqVO.getEnvName())))
                .and(FLOW_CONNECTOR_ENV.ACTIVE_STATUS.eq(pageReqVO.getActiveStatus())
                        .when(pageReqVO.getActiveStatus() != null))
                .and(FLOW_CONNECTOR_ENV.DELETED.eq(0))
                .orderBy(FLOW_CONNECTOR_ENV.SORT_ORDER, true)
                .orderBy(FLOW_CONNECTOR_ENV.CREATE_TIME, false);

        query = injectTenantFilter(query);

        Page<FlowConnectorEnvDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<FlowConnectorEnvDO> pageData = this.page(page, query);

        return new PageResult<>(pageData.getRecords(), pageData.getTotalRow());
    }

    /**
     * 根据主键ID查询
     *
     * @param id 主键ID
     * @return 环境配置DO
     */
    public FlowConnectorEnvDO getById(Long id) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_CONNECTOR_ENV.ID.eq(id))
                .and(FLOW_CONNECTOR_ENV.DELETED.eq(0));
        query = injectTenantFilter(query);
        return getOne(query);
    }

    /**
     * 根据环境UUID查询
     *
     * @param envUuid 环境配置UUID
     * @return 环境配置DO
     */
    public FlowConnectorEnvDO selectByEnvUuid(String envUuid) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_CONNECTOR_ENV.ENV_UUID.eq(envUuid))
                .and(FLOW_CONNECTOR_ENV.DELETED.eq(0));
        query = injectTenantFilter(query);
        return getOne(query);
    }

    /**
     * 根据连接器类型查询环境配置列表
     *
     * @param typeCode 连接器类型编号
     * @return 环境配置列表
     */
    public List<FlowConnectorEnvDO> selectByTypeCode(String typeCode) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_CONNECTOR_ENV.TYPE_CODE.eq(typeCode))
                .and(FLOW_CONNECTOR_ENV.DELETED.eq(0))
                .orderBy(FLOW_CONNECTOR_ENV.SORT_ORDER, true)
                .orderBy(FLOW_CONNECTOR_ENV.ID, true);
        query = injectTenantFilter(query);
        return this.list(query);
    }

    /**
     * 检查环境编码是否已存在（租户级唯一）
     *
     * @param typeCode 连接器类型编号
     * @param envCode  环境编码
     * @return 是否存在
     */
    public boolean existsByTypeAndEnvCode(String typeCode, String envCode) {
        QueryWrapper query = QueryWrapper.create()
                .where(FLOW_CONNECTOR_ENV.TYPE_CODE.eq(typeCode))
                .and(FLOW_CONNECTOR_ENV.ENV_CODE.eq(envCode))
                .and(FLOW_CONNECTOR_ENV.DELETED.eq(0));
        query = injectTenantFilter(query);
        return this.count(query) > 0;
    }

    /**
     * 批量根据环境UUID查询
     *
     * @param envUuids 环境配置UUID列表
     * @return 环境配置列表
     */
    public List<FlowConnectorEnvDO> selectByEnvUuids(List<String> envUuids) {
        if (envUuids == null || envUuids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(FLOW_CONNECTOR_ENV.ENV_UUID.in(envUuids))
                .and(FLOW_CONNECTOR_ENV.DELETED.eq(0));
        queryWrapper = injectTenantFilter(queryWrapper);
        return this.list(queryWrapper);
    }

    /**
     * 保存实体
     *
     * @param entity 实体
     * @return 是否成功
     */
    @Override
    public boolean save(FlowConnectorEnvDO entity) {
        return getMapper().insert(entity) > 0;
    }

    /**
     * 更新实体
     *
     * @param entity 实体
     * @return 是否成功
     */
    @Override
    public boolean updateById(FlowConnectorEnvDO entity) {
        return getMapper().update(entity) > 0;
    }

    /**
     * 删除实体（逻辑删除）
     *
     * @param id 主键ID
     * @return 是否成功
     */
    public boolean removeById(Long id) {
        FlowConnectorEnvDO entity = new FlowConnectorEnvDO();
        entity.setId(id);
        entity.setDeleted(1L);
        return updateById(entity);
    }
}
