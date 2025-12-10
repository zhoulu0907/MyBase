package com.cmsr.onebase.module.flow.core.flow;

import lombok.Data;

import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/12/10 8:37
 */
@Data
public class ExecutorInput {

    private String traceId;

    private Long processId;

    /**
     * 执行唯一标识，二次触发的时候需要传递
     */
    private String executionUuid;

    private Map<String, Object> inputParams;

    /**
     * 触发用户ID
     * 界面触发：登录用户
     * 后台触发： 流程的创建人
     */
    private Long triggerUserId;

    /**
     * 元数据接口调用传递过来的，也原样传递回去
     * SystemFieldConstants
     */
    private Map<String, String> systemFields;

}
