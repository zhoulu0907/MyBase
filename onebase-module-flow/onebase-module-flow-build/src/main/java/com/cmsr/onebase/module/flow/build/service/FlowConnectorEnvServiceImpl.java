package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.CreateFlowConnectorEnvReqVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorEnvReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorEnvRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorEnvReqVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 连接器环境配置 Service 实现
 * <p>
 * 实现环境配置的业务逻辑处理
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FlowConnectorEnvServiceImpl implements FlowConnectorEnvService {

    private final FlowConnectorEnvRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<FlowConnectorEnvVO> pageEnvs(PageConnectorEnvReqVO pageReqVO) {
        PageResult<FlowConnectorEnvDO> pageResult = repository.selectPage(pageReqVO);
        List<FlowConnectorEnvVO> voList = pageResult.getList().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    public FlowConnectorEnvVO getEnvDetail(Long id) {
        FlowConnectorEnvDO envDO = repository.getById(id);
        return convertToVO(envDO);
    }

    @Override
    public FlowConnectorEnvVO getEnvDetailByUuid(String envUuid) {
        FlowConnectorEnvDO envDO = repository.selectByEnvUuid(envUuid);
        return convertToVO(envDO);
    }

    @Override
    public List<FlowConnectorEnvVO> listByTypeCode(String typeCode) {
        List<FlowConnectorEnvDO> envList = repository.selectByTypeCode(typeCode);
        return envList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlowConnectorEnvVO createEnv(CreateFlowConnectorEnvReqVO createVO) {
        // 检查环境编码是否已存在
        if (repository.existsByTypeAndEnvCode(createVO.getTypeCode(),
                createVO.getEnvCode(), createVO.getApplicationId())) {
            throw new IllegalArgumentException("该连接器类型下已存在相同环境编码");
        }

        FlowConnectorEnvDO envDO = new FlowConnectorEnvDO();
        envDO.setEnvUuid(UUID.randomUUID().toString());
        envDO.setApplicationId(createVO.getApplicationId());
        envDO.setEnvName(createVO.getEnvName());
        envDO.setEnvCode(createVO.getEnvCode());
        envDO.setTypeCode(createVO.getTypeCode());
        envDO.setEnvUrl(createVO.getEnvUrl());
        envDO.setAuthType(createVO.getAuthType());
        envDO.setDescription(createVO.getDescription());
        envDO.setSortOrder(createVO.getSortOrder() != null ? createVO.getSortOrder() : 0);
        envDO.setActiveStatus(1);
        envDO.setLockVersion(0L);

        // 转换JSON字段
        if (createVO.getAuthConfig() != null) {
            envDO.setAuthConfig(createVO.getAuthConfig().toString());
        }
        if (createVO.getExtraConfig() != null) {
            envDO.setExtraConfig(createVO.getExtraConfig().toString());
        }

        repository.save(envDO);
        return convertToVO(envDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnv(UpdateFlowConnectorEnvReqVO updateVO) {
        FlowConnectorEnvDO existingEnv = repository.getById(updateVO.getId());
        if (existingEnv == null) {
            throw new IllegalArgumentException("环境配置不存在");
        }

        // 乐观锁检查
        if (!existingEnv.getLockVersion().equals(updateVO.getLockVersion())) {
            throw new IllegalStateException("数据已被修改，请刷新后重试");
        }

        FlowConnectorEnvDO envDO = new FlowConnectorEnvDO();
        envDO.setId(updateVO.getId());
        envDO.setEnvName(updateVO.getEnvName());
        envDO.setEnvCode(updateVO.getEnvCode());
        envDO.setEnvUrl(updateVO.getEnvUrl());
        envDO.setAuthType(updateVO.getAuthType());
        envDO.setDescription(updateVO.getDescription());
        envDO.setActiveStatus(updateVO.getActiveStatus());
        envDO.setSortOrder(updateVO.getSortOrder());
        envDO.setLockVersion(existingEnv.getLockVersion() + 1);

        if (updateVO.getAuthConfig() != null) {
            envDO.setAuthConfig(updateVO.getAuthConfig().toString());
        }
        if (updateVO.getExtraConfig() != null) {
            envDO.setExtraConfig(updateVO.getExtraConfig().toString());
        }

        repository.updateById(envDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        repository.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActiveStatus(Long id, Integer activeStatus) {
        FlowConnectorEnvDO envDO = new FlowConnectorEnvDO();
        envDO.setId(id);
        envDO.setActiveStatus(activeStatus);
        repository.updateById(envDO);
    }

    @Override
    public PageResult<FlowConnectorEnvLiteVO> listAll(PageParam pageParam) {
        PageConnectorEnvReqVO pageReqVO = new PageConnectorEnvReqVO();
        pageReqVO.setPageNo(pageParam.getPageNo());
        pageReqVO.setPageSize(pageParam.getPageSize());

        PageResult<FlowConnectorEnvDO> pageResult = repository.selectPage(pageReqVO);
        List<FlowConnectorEnvLiteVO> voList = pageResult.getList().stream()
                .map(this::convertToLiteVO)
                .collect(Collectors.toList());
        return new PageResult<>(voList, pageResult.getTotal());
    }

    /**
     * 转换为完整VO
     */
    private FlowConnectorEnvVO convertToVO(FlowConnectorEnvDO envDO) {
        if (envDO == null) {
            return null;
        }
        FlowConnectorEnvVO vo = new FlowConnectorEnvVO();
        BeanUtils.copyProperties(envDO, vo);

        try {
            if (envDO.getAuthConfig() != null) {
                vo.setAuthConfig(objectMapper.readTree(envDO.getAuthConfig()));
            }
            if (envDO.getExtraConfig() != null) {
                vo.setExtraConfig(objectMapper.readTree(envDO.getExtraConfig()));
            }
        } catch (Exception e) {
            log.error("JSON解析失败", e);
        }

        return vo;
    }

    /**
     * 转换为精简VO
     */
    private FlowConnectorEnvLiteVO convertToLiteVO(FlowConnectorEnvDO envDO) {
        if (envDO == null) {
            return null;
        }
        FlowConnectorEnvLiteVO vo = new FlowConnectorEnvLiteVO();
        vo.setEnvUuid(envDO.getEnvUuid());
        vo.setEnvName(envDO.getEnvName());
        vo.setEnvCode(envDO.getEnvCode());
        vo.setTypeCode(envDO.getTypeCode());
        vo.setEnvUrl(envDO.getEnvUrl());
        vo.setAuthType(envDO.getAuthType());
        vo.setDescription(envDO.getDescription());
        vo.setActiveStatus(envDO.getActiveStatus());
        vo.setCreateTime(envDO.getCreateTime());
        return vo;
    }
}
