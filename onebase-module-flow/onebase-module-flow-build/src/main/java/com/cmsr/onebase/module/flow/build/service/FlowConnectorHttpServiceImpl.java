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

    @Override
    public HttpActionVO getHttpAction(Long id) {
        FlowConnectorHttpDO connectorHttpDO = connectorHttpRepository.getById(id);
        if (connectorHttpDO == null) {
            throw new IllegalArgumentException("HTTP动作不存在: " + id);
        }

        return BeanUtils.toBean(connectorHttpDO, HttpActionVO.class);
    }

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
