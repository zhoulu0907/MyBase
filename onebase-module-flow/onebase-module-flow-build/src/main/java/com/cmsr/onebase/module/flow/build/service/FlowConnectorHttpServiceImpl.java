package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorHttpRepository;
import com.cmsr.onebase.module.flow.core.vo.CreateHttpActionReqVO;
import com.cmsr.onebase.module.flow.core.vo.HttpActionVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorHttpReqVO;
import com.cmsr.onebase.module.flow.core.vo.UpdateHttpActionReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorHttpTableDef.FLOW_CONNECTOR_HTTP;

/**
 * HTTP连接器动作Service实现
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Service
@Validated
public class FlowConnectorHttpServiceImpl implements FlowConnectorHttpService {

    @Setter
    private FlowConnectorHttpRepository connectorHttpRepository;

    /**
     * 创建HTTP动作
     * <p>
     * 将 HTTP 动作配置保存到数据库，自动生成 UUID 并设置默认值。
     *
     * <p>处理流程：
     * <ol>
     *   <li>将 VO 转换为 DO 对象</li>
     *   <li>自动生成唯一的 httpUuid（格式：http-xxxxxxxxxxxx）</li>
     *   <li>设置默认值：activeStatus=1（启用），sortOrder=0</li>
     *   <li>保存到数据库</li>
     *   <li>返回新创建记录的主键ID</li>
     * </ol>
     *
     * <p>事务管理：
     * 方法使用 @Transactional 注解，确保数据一致性。如果发生异常，整个操作会回滚。
     *
     * @param createReqVO 创建请求VO，包含HTTP动作的所有配置信息
     * @return 新创建的HTTP动作的主键ID
     * @throws IllegalArgumentException 如果请求参数验证失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHttpAction(@Valid CreateHttpActionReqVO createReqVO) {
        // 转换为DO
        FlowConnectorHttpDO connectorHttpDO = BeanUtils.toBean(createReqVO, FlowConnectorHttpDO.class);

        // 生成UUID
        connectorHttpDO.setHttpUuid("http-" + UUID.randomUUID().toString().replace("-", ""));

        // 设置默认值
        if (connectorHttpDO.getActiveStatus() == null) {
            connectorHttpDO.setActiveStatus(1);
        }
        if (connectorHttpDO.getSortOrder() == null) {
            connectorHttpDO.setSortOrder(0);
        }

        // 保存
        connectorHttpRepository.save(connectorHttpDO);

        return connectorHttpDO.getId();
    }

    /**
     * 更新HTTP动作
     * <p>
     * 根据主键ID更新HTTP动作的配置信息。
     *
     * <p>处理流程：
     * <ol>
     *   <li>根据ID查询HTTP动作是否存在</li>
     *   <li>如果不存在，抛出 IllegalArgumentException</li>
     *   <li>将 VO 转换为 DO 对象</li>
     *   <li>更新数据库记录</li>
     * </ol>
     *
     * <p>注意事项：
     * - 只更新传入的字段，未传入的字段保持原值不变
     * - httpUuid 不支持修改（在创建时生成）
     * - applicationId 不支持修改
     *
     * @param updateReqVO 更新请求VO，包含需要更新的字段（必须包含id）
     * @throws IllegalArgumentException 如果HTTP动作不存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHttpAction(@Valid UpdateHttpActionReqVO updateReqVO) {
        // 查询是否存在
        FlowConnectorHttpDO existHttpDO = connectorHttpRepository.getById(updateReqVO.getId());
        if (existHttpDO == null) {
            throw new IllegalArgumentException("HTTP动作不存在: " + updateReqVO.getId());
        }

        // 转换为DO并更新
        FlowConnectorHttpDO connectorHttpDO = BeanUtils.toBean(updateReqVO, FlowConnectorHttpDO.class);
        connectorHttpRepository.updateById(connectorHttpDO);
    }

    /**
     * 删除HTTP动作（软删除）
     * <p>
     * 将 HTTP 动作的 active_status 设置为 0，实现软删除。
     *
     * <p>软删除的优点：
     * - 保留历史记录，可追溯
     * - 可恢复（将 active_status 改回 1）
     * - 避免外键约束问题
     *
     * <p>处理流程：
     * <ol>
     *   <li>根据ID查询HTTP动作是否存在</li>
     *   <li>如果不存在，抛出 IllegalArgumentException</li>
     *   <li>将 activeStatus 设置为 0</li>
     *   <li>更新数据库记录</li>
     * </ol>
     *
     * <p>注意事项：
     * - 删除后的记录不会被普通查询接口查到
     * - 但仍保留在数据库中，可用于审计和恢复
     * - 实际的物理删除需要通过数据库管理员执行
     *
     * @param id HTTP动作的主键ID
     * @throws IllegalArgumentException 如果HTTP动作不存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHttpAction(Long id) {
        // 软删除：设置active_status为0
        FlowConnectorHttpDO connectorHttpDO = connectorHttpRepository.getById(id);
        if (connectorHttpDO == null) {
            throw new IllegalArgumentException("HTTP动作不存在: " + id);
        }

        connectorHttpDO.setActiveStatus(0);
        connectorHttpRepository.updateById(connectorHttpDO);
    }

    /**
     * 获取HTTP动作详情
     * <p>
     * 根据主键ID查询HTTP动作的完整配置信息。
     *
     * <p>处理流程：
     * <ol>
     *   <li>根据ID查询数据库记录</li>
     *   <li>如果不存在，抛出 IllegalArgumentException</li>
     *   <li>将 DO 转换为 VO 对象</li>
     *   <li>返回 VO 对象</li>
     * </ol>
     *
     * <p>注意事项：
     * - 此方法可以查询到已软删除的记录（activeStatus=0）
     * - 如果需要只查询启用状态的记录，请使用业务层的查询方法
     *
     * @param id HTTP动作的主键ID
     * @return HTTP动作的响应VO，包含所有配置信息
     * @throws IllegalArgumentException 如果HTTP动作不存在
     */
    @Override
    public HttpActionVO getHttpAction(Long id) {
        FlowConnectorHttpDO connectorHttpDO = connectorHttpRepository.getById(id);
        if (connectorHttpDO == null) {
            throw new IllegalArgumentException("HTTP动作不存在: " + id);
        }

        return BeanUtils.toBean(connectorHttpDO, HttpActionVO.class);
    }

    /**
     * 分页查询HTTP动作列表
     * <p>
     * 根据查询条件分页查询HTTP动作列表，支持多种过滤条件。
     *
     * <p>支持的查询条件：
     * <ul>
     *   <li>connectorUuid: 按连接器UUID过滤（精确匹配）</li>
     *   <li>httpName: 按HTTP动作名称过滤（模糊匹配）</li>
     *   <li>httpCode: 按HTTP动作编码过滤（模糊匹配）</li>
     *   <li>requestMethod: 按请求方法过滤（精确匹配，如GET/POST）</li>
     * </ul>
     *
     * <p>默认排序：
     * <ol>
     *   <li>第一优先级：sortOrder 升序（自定义排序）</li>
     *   <li>第二优先级：createTime 降序（创建时间）</li>
     * </ol>
     *
     * <p>过滤规则：
     * - 只返回 activeStatus=1 的记录（启用状态）
     * - 软删除的记录不会出现在结果中
     *
     * <p>处理流程：
     * <ol>
     *   <li>构建查询条件，默认只查询启用状态的记录</li>
     *   <li>根据请求参数动态添加过滤条件</li>
     *   <li>执行分页查询</li>
     *   <li>将 DO 列表转换为 VO 列表</li>
     *   <li>封装为 PageResult 返回</li>
     * </ol>
     *
     * @param pageReqVO 分页查询请求VO，包含分页参数和过滤条件
     * @return 分页结果，包含数据列表和总记录数
     */
    @Override
    public PageResult<HttpActionVO> getHttpActionPage(PageConnectorHttpReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_HTTP.SORT_ORDER.asc(), FLOW_CONNECTOR_HTTP.CREATE_TIME.desc());

        // 添加查询条件
        if (pageReqVO.getConnectorUuid() != null) {
            queryWrapper.and(FLOW_CONNECTOR_HTTP.CONNECTOR_UUID.eq(pageReqVO.getConnectorUuid()));
        }
        if (pageReqVO.getHttpName() != null && !pageReqVO.getHttpName().isEmpty()) {
            queryWrapper.and(FLOW_CONNECTOR_HTTP.HTTP_NAME.like(pageReqVO.getHttpName()));
        }
        if (pageReqVO.getHttpCode() != null && !pageReqVO.getHttpCode().isEmpty()) {
            queryWrapper.and(FLOW_CONNECTOR_HTTP.HTTP_CODE.like(pageReqVO.getHttpCode()));
        }
        if (pageReqVO.getRequestMethod() != null) {
            queryWrapper.and(FLOW_CONNECTOR_HTTP.REQUEST_METHOD.eq(pageReqVO.getRequestMethod()));
        }

        // 分页查询
        Page<FlowConnectorHttpDO> page = connectorHttpRepository.page(
                Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper
        );

        // 手动转换 DO 为 VO
        List<HttpActionVO> voList = page.getRecords().stream()
                .map(doObj -> BeanUtils.toBean(doObj, HttpActionVO.class))
                .collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotalRow());
    }
}
