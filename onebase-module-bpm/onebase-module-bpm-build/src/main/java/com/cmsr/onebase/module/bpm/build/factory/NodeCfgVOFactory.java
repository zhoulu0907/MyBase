package com.cmsr.onebase.module.bpm.build.factory;

import com.cmsr.onebase.module.bpm.core.vo.design.node.ApproverNodeVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.EndNodeVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.InitiationNodeVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.StartNodeVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeVO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 节点配置VO工厂类
 * 根据不同的节点类型创建对应的BaseNodeCfgVO子类
 *
 * @author liyang
 * @date 2025-01-23
 */
@Slf4j
public class NodeCfgVOFactory {

    /**
     * 根据节点类型创建对应的节点配置VO
     *
     * @param nodeType 节点类型
     * @return BaseNodeCfgVO 对应的子类实例
     */
    public static BaseNodeVO createNodeCfgVO(String nodeType) {
        BpmNodeTypeEnum nodeTypeEnum = BpmNodeTypeEnum.getByCode(nodeType);
        if (nodeTypeEnum == null) {
            log.warn("未知的节点类型: {}", nodeType);
            // 返回默认的基类
            return new BaseNodeVO();
        }

        return NODE_TYPE_MAPPING.get(nodeTypeEnum).get();
    }

    /**
     * 节点类型与配置VO的映射关系
     */
    private static final java.util.Map<BpmNodeTypeEnum, Supplier<BaseNodeVO>> NODE_TYPE_MAPPING =
        java.util.Map.of(
            // 发起节点
            BpmNodeTypeEnum.INITIATION, InitiationNodeVO::new,

            // 审批人节点
            BpmNodeTypeEnum.APPROVER, ApproverNodeVO::new,

            // 流程开始节点
            BpmNodeTypeEnum.START, StartNodeVO::new,

            // 流程结束节点
            BpmNodeTypeEnum.END, EndNodeVO::new

            // 其他节点类型可以根据需要添加
        );

    /**
     * 检查是否支持该节点类型
     *
     * @param nodeType 节点类型
     * @return 是否支持
     */
    public static boolean isSupported(String nodeType) {
        BpmNodeTypeEnum nodeTypeEnum = BpmNodeTypeEnum.getByCode(nodeType);
        return nodeTypeEnum != null && NODE_TYPE_MAPPING.containsKey(nodeTypeEnum);
    }
}
