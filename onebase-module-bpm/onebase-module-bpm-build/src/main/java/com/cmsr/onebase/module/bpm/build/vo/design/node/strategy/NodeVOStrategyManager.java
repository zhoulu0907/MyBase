package com.cmsr.onebase.module.bpm.build.vo.design.node.strategy;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.ApprovalModeEnum;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 节点配置VO策略管理器
 * 负责根据节点类型选择合适的策略来创建对应的配置VO
 *
 * @author liyang
 * @date 2025-10-23
 */
@Component
@Slf4j
public class NodeVOStrategyManager {

    @Resource
    private List<NodeVOStrategy<? extends BaseNodeVO, ? extends BaseNodeExtDTO>> strategies;

    /**
     * 节点类型与策略的映射关系
     */
    private Map<String, NodeVOStrategy<? extends BaseNodeVO, ? extends BaseNodeExtDTO>> strategyMap;

    /**
     * 初始化策略映射关系
     */
    @PostConstruct
    public void initStrategyMap() {
        strategyMap = new HashMap<>();

        for (NodeVOStrategy<? extends BaseNodeVO, ? extends BaseNodeExtDTO> strategy : strategies) {
            String nodeType = strategy.getSupportedNodeType();
            strategyMap.put(nodeType, strategy);
            log.info("注册节点配置策略: {} -> {}", nodeType, strategy.getClass().getSimpleName());
        }

        log.info("节点配置策略管理器初始化完成，共注册 {} 个策略", strategyMap.size());
    }

    /**
     * 根据节点类型创建对应的节点配置VO
     *
     * @param extData 扩展数据
     * @return BaseNodeVO 对应的子类实例
     */
    public BaseNodeVO createNodeVO(String extData) {
        if (StringUtils.isBlank(extData)) {
            log.warn("扩展数据为空，返回默认配置VO");
            return new BaseNodeVO();
        }

        // 先按照基类解析获取类型
        BaseNodeExtDTO baseNodeExtDTO = JsonUtils.parseObject(extData, BaseNodeExtDTO.class);
        if (baseNodeExtDTO == null) {
            log.warn("解析节点扩展数据为空，返回默认配置VO");
            return new BaseNodeVO();
        }

        String nodeType = baseNodeExtDTO.getNodeType();

        if (StringUtils.isBlank(nodeType)) {
            log.warn("节点类型为空，返回默认配置VO");
            return new BaseNodeVO();
        }

        NodeVOStrategy<? extends BaseNodeVO, ? extends BaseNodeExtDTO> strategy = strategyMap.get(nodeType.toLowerCase());
        if (strategy == null) {
            log.warn("未找到节点类型 '{}' 对应的策略，返回默认配置VO", nodeType);
            return new BaseNodeVO();
        }

        try {
            BaseNodeVO nodeVO = strategy.createNodeVO();

            // 设置通用属性
            nodeVO.setMeta(baseNodeExtDTO.getMeta());
            nodeVO.setType(baseNodeExtDTO.getNodeType());

            // 使用原始类型调用parseExtData方法，避免通配符捕获问题
            @SuppressWarnings("unchecked")
            NodeVOStrategy<BaseNodeVO, BaseNodeExtDTO> rawStrategy = (NodeVOStrategy<BaseNodeVO, BaseNodeExtDTO>) strategy;

            // 处理特殊属性
            rawStrategy.parseExtData(nodeVO, extData);

            log.debug("成功创建节点配置VO: {} -> {}", nodeType, nodeVO.getClass().getSimpleName());
            return nodeVO;
        } catch (Exception e) {
            log.error("创建节点配置VO失败: {}", nodeType, e);
            return new BaseNodeVO();
        }
    }

    /**
     * 检查是否支持该节点类型
     *
     * @param nodeType 节点类型
     * @return 是否支持
     */
    public boolean isSupported(String nodeType) {
        return nodeType != null && strategyMap.containsKey(nodeType.toLowerCase());
    }

    /**
     * 获取所有支持的节点类型
     *
     * @return 支持的节点类型列表
     */
    public List<String> getSupportedNodeTypes() {
        return List.copyOf(strategyMap.keySet());
    }

    /**
     * 填充节点扩展数据
     * 将BpmDefJsonVO.NodeVO的数据转换为NodeJson.ext格式
     *
     * @param nodeVO 节点配置VO
     * @return 扩展数据JSON字符串
     */
    public void fillNodeExtData(NodeJson nodeJson, BaseNodeVO nodeVO, Long appId) {
        if (nodeVO == null) {
            log.error("节点VO为空");
            throw exception(ErrorCodeConstants.MISSING_NODE_VO_DATA);
        }

        String nodeType = nodeVO.getType();

        NodeVOStrategy<? extends BaseNodeVO, ? extends BaseNodeExtDTO> strategy = strategyMap.get(nodeType.toLowerCase());
        if (strategy == null) {
            log.error("未找到节点类型 '{}' 对应的策略，返回默认扩展数据", nodeType);
            throw exception(ErrorCodeConstants.UNSUPPORT_NODE_TYPE);
        }

        // 使用策略构建扩展数据
        @SuppressWarnings("unchecked")
        NodeVOStrategy<BaseNodeVO, BaseNodeExtDTO> rawStrategy = (NodeVOStrategy<BaseNodeVO, BaseNodeExtDTO>) strategy;
        BaseNodeExtDTO extData = rawStrategy.buildExtData(nodeVO, appId);

        String ext = JsonUtils.toJsonString(extData);
        nodeJson.setExt(ext);

        // 权限
        NodePermFlagDTO permFlagDTO = rawStrategy.buildPermissionFlag(extData);

        if (permFlagDTO != null) {
            nodeJson.setPermissionFlag(JsonUtils.toJsonString(permFlagDTO));
        }

        if (extData instanceof ApproverNodeExtDTO approverNodeExtDTO) {

            String approvalMode = approverNodeExtDTO.getApproverConfig().getApprovalMode();

            // 设置会签参数，默认为或签
            if (Objects.equals(approvalMode, ApprovalModeEnum.COUNTER_SIGN.getCode())) {
                nodeJson.setNodeRatio(new BigDecimal("100"));
            }
        }
    }
}
