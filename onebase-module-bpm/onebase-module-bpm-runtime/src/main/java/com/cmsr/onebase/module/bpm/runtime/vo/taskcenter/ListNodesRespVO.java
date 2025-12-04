package com.cmsr.onebase.module.bpm.runtime.vo.taskcenter;

import lombok.Data;

import java.util.List;

/**
 * 节点列表返回结果
 *
 * @author liyang
 * @date 2025-11-04
 */
@Data
public class ListNodesRespVO {
    /**
     * 节点列表
     */
    private List<NodeVO> nodes;

    @Data
    public static class NodeVO {
        /**
         * 节点code
         */
        private String nodeCode;

        /**
         * 节点名称
         */
        private String nodeName;

        /**
         * 节点类型
         */
        private String nodeType;
    }
}
