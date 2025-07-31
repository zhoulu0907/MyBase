package com.cmsr.onebase.module.app.controller.appresource;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.annotation.security.PermitAll;
import com.cmsr.onebase.framework.common.pojo.CommonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName HealthCheckController
 * @Description 健康检查控制器，提供应用健康状态检查接口
 * @Author mickey
 * @Date 2025/6/27 15:28
 */
@Slf4j
@Tag(name = "应用资源管理-健康检查")
@RestController
@RequestMapping("/app/appresource")
@Validated

public class HealthCheckController {
    @PermitAll
    @GetMapping("/health-check")
    public CommonResult healthCheck() {
        return success(true);
    }
}
