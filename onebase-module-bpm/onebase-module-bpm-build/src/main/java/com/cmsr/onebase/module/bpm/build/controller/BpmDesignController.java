package com.cmsr.onebase.module.bpm.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.build.service.BpmDesignService;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmPublishReqVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author liyang
 * @date 2025-10-21
 */
@Tag(name = "审批流设计")
@RestController
@RequestMapping("/bpm/design")
@Validated
@Slf4j
public class BpmDesignController {

     @Resource
     private BpmDesignService bpmDesignService;

    @PostMapping("/save")
    @Operation(summary = "保存流程")
    public CommonResult<Long> saveFlow(@Valid @RequestBody BpmDesignVO flowDesignVO) {
        log.info("流程请求信息: {}", flowDesignVO);
        Long flowId = bpmDesignService.save(flowDesignVO);
        return CommonResult.success(flowId);
    }

    @GetMapping("/get")
    @Operation(summary = "获取流程")
    public CommonResult<BpmDesignVO> query(@RequestParam("id") Long flowId) {
        log.info("查询流程: {}", flowId);
        BpmDesignVO flowDesignVO = bpmDesignService.queryById(flowId);
        return CommonResult.success(flowDesignVO);
    }

    @GetMapping("/get-by-business-id")
    @Operation(summary = "根据业务ID查询默认流程")
    public CommonResult<BpmDesignVO> queryByBusinessId(@RequestParam("businessId") Long businessId) {
        log.info("查询流程: {}", businessId);
        BpmDesignVO flowDesignVO = bpmDesignService.queryByBusinessId(businessId);
        return CommonResult.success(flowDesignVO);
    }

    @PostMapping("/publish")
    @Operation(summary = "发布流程")
    public CommonResult<Boolean> publish(@Valid @RequestBody BpmPublishReqVO reqVo) {
        log.info("发布流程: {}", reqVo);
        bpmDesignService.publish(reqVo);
        return CommonResult.success(true);
    }
    public static void main(String[] args) {
        String normalJson = "{\n" +
                "\t\"nodes\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"type\": \"start\",\n" +
                "\t\t\t\"id\": \"1\",\n" +
                "\t\t\t\"name\": \"开始\",\n" +
                "\t\t\t\"meta\": {\n" +
                "\t\t\t\t\"position\": {\n" +
                "\t\t\t\t\t\"x\": 0,\n" +
                "\t\t\t\t\t\"y\": 9\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"type\": \"initiation\",\n" +
                "\t\t\t\"id\": \"2\",\n" +
                "\t\t\t\"name\": \"发起节点\",\n" +
                "\t\t\t\"meta\": {\n" +
                "\t\t\t\t\"position\": {\n" +
                "\t\t\t\t\t\"x\": 0,\n" +
                "\t\t\t\t\t\"y\": 9\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t\"data\": {\n" +
                "\t\t\t\t\"deptConfig\": {\n" +
                "\t\t\t\t\t\"useCustomDept\": true,\n" +
                "\t\t\t\t\t\"deptId\": \"111\",\n" +
                "\t\t\t\t\t\"deptName\": \"部门A\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"type\": \"approver\",\n" +
                "\t\t\t\"id\": \"3\",\n" +
                "\t\t\t\"name\": \"组长审批\",\n" +
                "\t\t\t\"meta\": {\n" +
                "\t\t\t\t\"position\": {\n" +
                "\t\t\t\t\t\"x\": 0,\n" +
                "\t\t\t\t\t\"y\": 9\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t\"data\": {\n" +
                "\t\t\t\t\"approverConfig\": {\n" +
                "\t\t\t\t\t\"approverType\": \"user\",\n" +
                "\t\t\t\t\t\"users\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"userId\": \"111\",\n" +
                "\t\t\t\t\t\t\t\"name\": \"张三\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"userId\": \"222\",\n" +
                "\t\t\t\t\t\t\t\"name\": \"李四\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t],\n" +
                "\t\t\t\t\t\"approvalMode\": \"any_sign\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"buttonConfigs\": [\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"buttonType\": \"approve\",\n" +
                "\t\t\t\t\t\t\"buttonName\": \"同意\",\n" +
                "\t\t\t\t\t\t\"displayName\": \"同意\",\n" +
                "\t\t\t\t\t\t\"defaultApprovalComment\": \"同意该申请\",\n" +
                "\t\t\t\t\t\t\"approvalCommentRequired\": false,\n" +
                "\t\t\t\t\t\t\"enabled\": true,\n" +
                "\t\t\t\t\t\t\"batchApproval\": true\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"buttonType\": \"reject\",\n" +
                "\t\t\t\t\t\t\"buttonName\": \"拒绝\",\n" +
                "\t\t\t\t\t\t\"displayName\": \"拒绝\",\n" +
                "\t\t\t\t\t\t\"defaultApprovalComment\": \"拒绝该申请\",\n" +
                "\t\t\t\t\t\t\"approvalCommentRequired\": true,\n" +
                "\t\t\t\t\t\t\"enabled\": true,\n" +
                "\t\t\t\t\t\t\"batchApproval\": false\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t],\n" +
                "\t\t\t\t\"fieldPermConfig\": {\n" +
                "\t\t\t\t\t\"useNodeConfig\": true,\n" +
                "\t\t\t\t\t\"fieldConfigs\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"fieldId\": \"1\",\n" +
                "\t\t\t\t\t\t\t\"fieldName\": \"申请人姓名\",\n" +
                "\t\t\t\t\t\t\t\"fieldPermType\": \"read\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"fieldId\": \"2\",\n" +
                "\t\t\t\t\t\t\t\"fieldName\": \"所属部门\",\n" +
                "\t\t\t\t\t\t\t\"fieldPermType\": \"read\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"fieldId\": \"3\",\n" +
                "\t\t\t\t\t\t\t\"fieldName\": \"申请事由\",\n" +
                "\t\t\t\t\t\t\t\"fieldPermType\": \"read\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"fieldId\": \"4\",\n" +
                "\t\t\t\t\t\t\t\"fieldName\": \"申请金额\",\n" +
                "\t\t\t\t\t\t\t\"fieldPermType\": \"read\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"fieldId\": \"5\",\n" +
                "\t\t\t\t\t\t\t\"fieldName\": \"审批备注\",\n" +
                "\t\t\t\t\t\t\t\"fieldPermType\": \"write\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"advancedConfig\": {\n" +
                "\t\t\t\t\t\"autoApprove\": {\n" +
                "\t\t\t\t\t\t\"initAutoApprove\": true,\n" +
                "\t\t\t\t\t\t\"dupUserAutoApprove\": false,\n" +
                "\t\t\t\t\t\t\"adjDupUserAutoApprove\": true\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"autoApproveIsEmpty\": \"skip\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"type\": \"end\",\n" +
                "\t\t\t\"id\": \"6\",\n" +
                "\t\t\t\"name\": \"结束\",\n" +
                "\t\t\t\"meta\": {\n" +
                "\t\t\t\t\"position\": {\n" +
                "\t\t\t\t\t\"x\": 0,\n" +
                "\t\t\t\t\t\"y\": 9\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"edges\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"sourceNodeID\": \"1\",\n" +
                "\t\t\t\"targetNodeID\": \"2\",\n" +
                "\t\t\t\"type\": \"PASS\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"sourceNodeID\": \"2\",\n" +
                "\t\t\t\"targetNodeID\": \"3\",\n" +
                "\t\t\t\"type\": \"PASS\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"sourceNodeID\": \"3\",\n" +
                "\t\t\t\"targetNodeID\": \"6\",\n" +
                "\t\t\t\"type\": \"PASS\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";

        // 去除 \n 和 \t 并压缩成一行
        String singleLineJson = normalJson.replaceAll("[\\n\\t]", "");

        // 使用 ObjectMapper 进行转义处理
        ObjectMapper mapper = new ObjectMapper();
        try {
            String escapedJson = mapper.writeValueAsString(singleLineJson);

            System.out.println("单行且转义后的JSON:");
            System.out.println(escapedJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}