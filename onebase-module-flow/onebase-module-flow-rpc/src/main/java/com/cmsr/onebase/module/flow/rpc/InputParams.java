package com.cmsr.onebase.module.flow.rpc;

/**
 * @Author：huangjie
 * @Date：2025/10/14 9:53
 */

public class InputParams {

    private String jobType;

    private Long processId;

    private String redisAddress;

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getRedisAddress() {
        return redisAddress;
    }

    public void setRedisAddress(String redisAddress) {
        this.redisAddress = redisAddress;
    }

}