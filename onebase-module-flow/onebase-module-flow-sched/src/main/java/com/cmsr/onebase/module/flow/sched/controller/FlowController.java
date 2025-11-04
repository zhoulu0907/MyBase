package com.cmsr.onebase.module.flow.sched.controller;

import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.ExecutorRequest;
import com.cmsr.onebase.module.flow.core.flow.FlowExecuteProvider;
import com.cmsr.onebase.module.flow.sched.controller.vo.RunFlowReq;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：huangjie
 * @Date：2025/10/17 14:47
 */
@Setter
@RequestMapping("/flow")
@RestController
public class FlowController {

    @Autowired
    private FlowExecuteProvider flowExecuteProvider;

    @PostMapping("/run")
    public ResponseEntity<String> runFlow(@RequestBody RunFlowReq runFlowReq) {
        ExecutorRequest jobMessage = new ExecutorRequest();
        jobMessage.setJobType(runFlowReq.getJobType());
        jobMessage.setProcessId(runFlowReq.getProcessId());
        ExecutorResult executorResult = flowExecuteProvider.executeFlow(jobMessage);
        if (executorResult.isSuccess()) {
            return ResponseEntity.ok(executorResult.toString());
        } else {
            return ResponseEntity.badRequest().body(executorResult.toString());
        }
    }
}
