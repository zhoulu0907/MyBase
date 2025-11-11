package com.cmsr.onebase.module.bpm.runtime.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程实例的操作记录响应VO
 *
 * @author liyang
 * @date 2025-10-29
 */
@Data
public class BpmOperatorRecordRespVO {
    @Data
    public static class OperatorRecord {
        /**
         * 节点名称
         */
        private String nodeName;

        /**
         * 节点类型
         */
        private String nodeType;

        /**
         * 展示状态
         */
        private String displayStatus;

        /**
         * 审批方式，仅审批类型节点展示
         */
        private String approveMode;

        /**
         * 操作人信息
         */
        private List<OperatorInfo> operators;
    }

    @Data
    public static class OperatorInfo {
        /**
         * 处理人
         */
        private String operator;

        /**
         * 处理人头像
         */
        private String avatar;

        /**
         * 处理时间
         */
        private LocalDateTime operatorTime;

        /**
         * 任务状态
         */
        private String taskStatus;

        /**
         * 审批意见
         */
        private String comment;
    }
}
