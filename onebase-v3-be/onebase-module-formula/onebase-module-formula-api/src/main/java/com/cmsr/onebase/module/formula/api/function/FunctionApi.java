package com.cmsr.onebase.module.formula.api.function;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.module.formula.api.function.dto.FunctionRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = "formula-server")
@Tag(name = "RPC 服务 - 函数")
public interface FunctionApi {

    String PREFIX = "/rpc-api/formula/function";

    @GetMapping("/rpc-api/formula/function/get")
    @Operation(summary = "获得函数信息")
    @Parameter(name = "id", description = "函数编号", example = "1024", required = true)
    CommonResult<FunctionRespDTO> getFunction(@RequestParam("id") Long id);

    @GetMapping("/rpc-api/formula/function/list")
    @Operation(summary = "获得函数信息数组")
    @Parameter(name = "ids", description = "函数编号数组", example = "1,2", required = true)
    CommonResult<List<FunctionRespDTO>> getFunctionList(@RequestParam("ids") Collection<Long> ids);

    @GetMapping("/rpc-api/formula/function/valid")
    @Operation(summary = "校验函数是否合法")
    @Parameter(name = "ids", description = "函数编号数组", example = "1,2", required = true)
    CommonResult<Boolean> validateFunctionList(@RequestParam("ids") Collection<Long> ids);

    /**
     * 获得指定编号的函数 Map
     *
     * @param ids 函数编号数组
     * @return 函数 Map
     */
    default Map<Long, FunctionRespDTO> getFunctionMap(Collection<Long> ids) {
        List<FunctionRespDTO> list = getFunctionList(ids).getCheckedData();
        return CollectionUtils.convertMap(list, FunctionRespDTO::getId);
    }

}
