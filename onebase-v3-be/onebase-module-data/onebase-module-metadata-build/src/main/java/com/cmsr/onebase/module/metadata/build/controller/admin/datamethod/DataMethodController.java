package com.cmsr.onebase.module.metadata.build.controller.admin.datamethod;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodDetailQueryReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodQueryReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.build.service.datamethod.MetadataDataMethodQueryBuildService;
import com.cmsr.onebase.module.metadata.build.service.datamethod.vo.DataMethodQueryVO;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 数据方法管理（构建端）
 * 仅保留方法列表与详情等构建管理接口；
 * 系统级动态数据操作接口已迁至 runtime 模块。
 */
@Tag(name = "管理后台 - 数据方法管理（构建端）")
@RestController
@RequestMapping("/metadata/data-method")
@Validated
public class DataMethodController {

    @Resource
    private MetadataDataMethodQueryBuildService dataMethodService;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @PostMapping("/list")
    @Operation(summary = "查询业务实体的数据方法列表")
    public CommonResult<List<DataMethodRespVO>> getDataMethodList(@Valid @RequestBody DataMethodQueryReqVO reqVO) {
        // ID与UUID兼容处理
        String entityUuid = idUuidConverter.resolveEntityUuid(reqVO.getEntityUuid(), reqVO.getEntityId());
        DataMethodQueryVO queryVO = new DataMethodQueryVO(entityUuid, reqVO.getMethodType(), reqVO.getKeyword());
        List<DataMethodRespVO> methods = dataMethodService.getDataMethodList(queryVO);
        return success(methods);
    }

    @PostMapping("/detail")
    @Operation(summary = "获取指定数据方法的详细信息")
    public CommonResult<DataMethodDetailRespVO> getDataMethodDetail(@Valid @RequestBody DataMethodDetailQueryReqVO reqVO) {
        // ID与UUID兼容处理
        String entityUuid = idUuidConverter.resolveEntityUuid(reqVO.getEntityUuid(), reqVO.getEntityId());
        DataMethodDetailRespVO detail = dataMethodService.getDataMethodDetail(entityUuid, reqVO.getMethodCode());
        return success(detail);
    }
}
