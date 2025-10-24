package com.cmsr.onebase.module.formula.api.formula;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteReqDTO;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "formula-server")
@Tag(name = "RPC 服务 - 公式引擎")
public interface FormulaEngineApi {

    String PREFIX = "/rpc-api/formula/engine";

    @PostMapping(PREFIX+"/execute-formula")
    @Operation(summary = "获得函数信息")
    @Parameter(name = "reqDTO", description = "执行公式计算", required = true)
    CommonResult<FormulaExecuteRespDTO> executeFormula(@Valid @RequestBody FormulaExecuteReqDTO reqDTO);

}
