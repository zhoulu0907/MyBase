package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.executor.DashMetaDataCardExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
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
import java.util.regex.Pattern;

@Tag(name = "运行时 - 数据卡片接口")
@RestController
@RequestMapping("/metadata/data-card")
@Validated
public class DataCardRuntimeController {

    @Resource
    private DashMetaDataCardExecutor dashMetaDataCardExecutor;

    /**
     * UUID 格式正则表达式，用于验证 traceId
     */
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    @PostMapping("/query")
    @Operation(summary = "数据卡片批量查询")
    @ApiSignIgnore
    @PermitAll
    public CommonResult<List<Map<String, Object>>> query(@RequestBody List<Map<String, Object>> cards,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        String traceId = request.getHeader("X-Trace-Id");
        // XSS 防护：验证 traceId 格式，只允许合法的 UUID 格式
        if (StringUtils.isBlank(traceId) || !UUID_PATTERN.matcher(traceId).matches()) {
            traceId = UUID.randomUUID().toString();
        }
        List<Map<String, Object>> result = dashMetaDataCardExecutor.execute(cards, traceId);
        response.setHeader("X-Trace-Id", traceId);
        return CommonResult.success(result);
    }
}

