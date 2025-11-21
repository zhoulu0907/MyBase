package com.cmsr.onebase.module.bpm.runtime.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
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
         * 已完成的， 显示的是完成时间；
         * 进行中的，显示是到达/创建时间；
         * 未开始的，则为空；
         */
        private LocalDateTime displayTime;

        /**
         * 等待时间描述，目前仅当前节点展示
         */
        private String waitTimeDesc;

        /**
         * 审批方式，仅审批类型节点展示
         */
        private String approveMode;

        /**
         * 是否当前节点
         */
        @JsonProperty("isCurrent")
        private boolean isCurrent;

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
         * 代理人
         */
        private String agent;

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

        /**
         * 是否已读
         */
        private boolean viewed;
    }
}
