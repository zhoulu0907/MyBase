package com.cmsr.onebase.server.controller;

import com.cmsr.onebase.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.module.infra.service.logger.ApiAccessLogService;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 默认 Controller，解决部分 module 未开启时的 404 提示。
 * 例如说，/bpm/** 路径，工作流
 *
 * @author matianyu
 * @date 2025-09-16
 */
@RestController
@Slf4j
public class DefaultController {

    @Resource
    private ApiAccessLogService apiAccessLogService;

    /**
     * 测试接口：打印 query、header、body
     */
    @RequestMapping(value = { "/test" })
    @PermitAll
    public CommonResult<Boolean> test(HttpServletRequest request) {
        // 打印查询参数
        log.info("Query: {}", ServletUtils.getParamMap(request));
        // 打印请求头
        log.info("Header: {}", ServletUtils.getHeaderMap(request));
        // 打印请求体
        log.info("Body: {}", ServletUtils.getBody(request));
        return success(true);
    }

    /**
     * 简单的默认接口，用于前端传递任意参数进行测试
     * 
     * @param data 前端传递的任意字符串参数
     * @param request HTTP请求对象
     * @return 处理结果
     */
    @PostMapping("/default")
    @PermitAll
    public CommonResult<String> defaultApi(@RequestBody @NotEmpty(message = "参数不能为空") String data, 
                                          HttpServletRequest request) {
        LocalDateTime beginTime = LocalDateTime.now();
        
        // 在日志中打印接收到的参数
        log.info("接收到前端参数：{}", data);
        log.info("请求来源IP：{}", ServletUtils.getClientIP(request));
        log.info("User-Agent：{}", ServletUtils.getUserAgent(request));
        
        // 创建API访问日志并保存到数据库
        try {
            LocalDateTime endTime = LocalDateTime.now();
            
            ApiAccessLogCreateReqDTO logDTO = new ApiAccessLogCreateReqDTO();
            logDTO.setApplicationName("OneBase3");
            logDTO.setRequestMethod("POST");
            logDTO.setRequestUrl("/default");
            logDTO.setRequestParams(data);
            logDTO.setResponseBody("success");
            logDTO.setUserIp(ServletUtils.getClientIP(request) != null ? ServletUtils.getClientIP(request) : "127.0.0.1");
            logDTO.setUserAgent(ServletUtils.getUserAgent(request) != null ? ServletUtils.getUserAgent(request) : "Unknown");
            logDTO.setOperateModule("系统管理");
            logDTO.setOperateName("默认接口测试");
            logDTO.setOperateType(1); // 查询操作
            logDTO.setBeginTime(beginTime);
            logDTO.setEndTime(endTime);
            logDTO.setDuration((int) java.time.Duration.between(beginTime, endTime).toMillis());
            logDTO.setResultCode(0);
            logDTO.setResultMsg("操作成功");
            
            // 保存日志到数据库，使用默认租户ID (0) 来避免租户约束问题
            apiAccessLogService.createApiAccessLog(logDTO);
            
            log.info("日志已成功保存到数据库");
        } catch (Exception e) {
            log.error("保存日志到数据库失败", e);
            // 即使日志保存失败，也不影响接口正常返回
        }
        
        return success("参数接收成功，已记录到日志：" + data);
    }

}
