package com.cmsr.onebase.module.bpm.runtime.vo;

import lombok.Data;

@Data
public class BpmFlowInstanceExtVO {
    //流程信息
    private ProcessInfo processInfo;
    //应用ID
    private String appId;
    @Data
    public class ProcessInfo {
        //流程标题
        private String processTitle;
        //流程发起人
        private String initiator;
        //流程发起人部门
        private String initiatorDepartment;
        //发起时间
        private String submitTime;
        //表单摘要
        private String formSummary;
        //流程表单
        private String formName;
        //流程版本号
        private String processVersion;
    }
}
