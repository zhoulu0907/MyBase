package com.cmsr.onebase.module.metadata.build.semantic.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.semantic.executor.DashMetaDataCardExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "构建态 - 数据卡片接口")
@RestController
@RequestMapping("/build/metadata/data-card")
@Validated
public class DataCardBuildController {

    @Resource(name = "buildDashMetaDataCardExecutor")
    private DashMetaDataCardExecutor dashMetaDataCardExecutor;

    @PostMapping("/query")
    @Operation(summary = "数据卡片批量查询(构建态)")
    public CommonResult<List<Map<String, Object>>> query(@RequestBody List<Map<String, Object>> cards,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        List<Map<String, Object>> result = dashMetaDataCardExecutor.execute(cards, traceId);
        response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(result);
    }
}
