package com.cmsr.onebase.module.flow.message;

/**
 * @Author：huangjie
 * @Date：2025/10/14 9:53
 */

public class InputParams {

    private String endpoints;

    private String topic;

    private Long processId;

    private String msgTag;

    public String getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(String endpoints) {
        this.endpoints = endpoints;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getMsgTag() {
        return msgTag;
    }

    public void setMsgTag(String msgTag) {
        this.msgTag = msgTag;
    }
}
