package com.cmsr.onebase.module.metadata.controller.admin.datamethod;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailQueryReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodQueryReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 数据方法管理
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 数据方法管理")
@RestController
@RequestMapping("/metadata/data-method")
@Validated
public class DataMethodController {

    @Resource
    private MetadataDataMethodService dataMethodService;

    @GetMapping("/list")
    @Operation(summary = "查询业务实体的数据方法列表")
    public CommonResult<List<DataMethodRespVO>> getDataMethodList(@Valid DataMethodQueryReqVO reqVO) {
        List<DataMethodRespVO> methods = dataMethodService.getDataMethodList(reqVO.getEntityId(), reqVO.getMethodType(), reqVO.getKeyword());
        return success(methods);
    }

    @GetMapping("/detail")
    @Operation(summary = "获取指定数据方法的详细信息")
    public CommonResult<DataMethodDetailRespVO> getDataMethodDetail(@Valid DataMethodDetailQueryReqVO reqVO) {
        DataMethodDetailRespVO detail = dataMethodService.getDataMethodDetail(reqVO.getEntityId(), reqVO.getMethodCode());
        return success(detail);
    }

} 