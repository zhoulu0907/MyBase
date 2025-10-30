package com.cmsr.onebase.module.metadata.api.datamethod.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 调试用 - 元数据动态数据查询接口（仅用于临时调试 getDataByCondition）
 * 注意：调试完成后建议删除该 Controller，避免在生产环境暴露不必要接口。
 *
 * 路径规范：/metadata/data-method-debug/query
 *
 * @author bty418
 * @date 2025-10-23
 */
@Tag(name = "调试 - 动态数据查询接口")
@RestController
@RequestMapping("/metadata/data-method-debug")
@Slf4j
public class DataMethodDebugController {

    @Resource
    private DataMethodApi dataMethodApi;

    /**
     * 调试查询接口：调用 DataMethodApi#getDataByCondition
     *
     * @param reqDTO 查询请求 DTO（支持多组嵌套条件）
     * @return 二维结果列表：外层 List 行；内层 List 行内字段
     */
    @PostMapping("/query")
    @Operation(summary = "调试 - 条件查询动态数据")
    @PermitAll // 调试阶段放开；如需权限控制可替换为 @PreAuthorize
    public CommonResult<List<List<EntityFieldDataRespDTO>>> debugQuery(@Valid @RequestBody EntityFieldDataReqDTO reqDTO) {
        long start = System.currentTimeMillis();
        log.info("[DEBUG-QUERY] 接收到调试查询请求 entityId={}, conditionsGroupSize={}, num={}",
                reqDTO.getEntityId(),
                reqDTO.getConditionDTO() == null ? 0 : reqDTO.getConditionDTO().size(),
                reqDTO.getNum());
        List<List<EntityFieldDataRespDTO>> result = dataMethodApi.getDataByCondition(reqDTO);
        log.info("[DEBUG-QUERY] 查询完成 rows={} costMs={}", result.size(), System.currentTimeMillis() - start);
        return success(result);
    }
}

