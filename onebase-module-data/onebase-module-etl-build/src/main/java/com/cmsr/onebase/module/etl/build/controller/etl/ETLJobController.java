package com.cmsr.onebase.module.etl.build.controller.etl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.ETLJobService;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLBriefVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLPageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据工厂 - 数据流管理")
@RestController
@RequestMapping("/etl/job")
@Validated
public class ETLJobController {

    @Resource
    private ETLJobService etlJobService;

    @GetMapping("/page")
    @Operation(summary = "分页查询数据流")
    public CommonResult<PageResult<ETLBriefVO>> pageQueryFlow(@Validated ETLPageReqVO pageReqVO) {
        return CommonResult.success(etlJobService.getETLPage(pageReqVO));
    }


}
