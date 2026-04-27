package com.cmsr.onebase.module.bpm.core.dto;

import lombok.Data;

/**
 * 流程信息扩展DTO
 * 
 * 用于存储流程实例的详细信息，存储在ext字段中
 * 
 * @author liyang
 * @date 2025-10-28
 */
@Data
public class BpmProcessInfoDTO {
    
    /**
     * 流程标题
     */
    private String processTitle;

    /**
     * 发起人
     */
    private String initiator;

    /**
     * 发起部门
     */
    private String initiatorDepartment;

    /**
     * 发起时间
     */
    private String submitTime;

    /**
     * 表单摘要
     */
    private String formSummary;

    /**
     * 流程表单名称
     */
    private String formName;

    /**
     * 流程版本号
     */
    private String processVersion;

    /**
     * 流程状态
     * 0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回
     */
    private String flowStatus;
}

