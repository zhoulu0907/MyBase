package com.cmsr.onebase.module.metadata.api.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限管理 API 实现
 *
 * @author matianyu
 * @date 2025-09-10
 */
@RestController
@Validated
@Slf4j
public class MetadataPermitApiImpl implements MetadataPermitApi {

    @Override
    public CommonResult<List<PermitRefOtftRespDTO>> getPermitRefOtftList() {
        try {
            log.info("获取权限参考操作类型列表");
            
            // 返回一些模拟数据，实际应该从数据库查询
            List<PermitRefOtftRespDTO> result = new ArrayList<>();
            
            // 字符串类型操作
            PermitRefOtftRespDTO readOp = new PermitRefOtftRespDTO();
            readOp.setId(1L);
            readOp.setOperationTypeCode("READ");
            readOp.setOperationTypeName("查看");
            readOp.setFieldTypeCode("STRING");
            readOp.setSort(1);
            readOp.setRemark("查看操作权限");
            result.add(readOp);
            
            PermitRefOtftRespDTO editOp = new PermitRefOtftRespDTO();
            editOp.setId(2L);
            editOp.setOperationTypeCode("EDIT");
            editOp.setOperationTypeName("编辑");
            editOp.setFieldTypeCode("STRING");
            editOp.setSort(2);
            editOp.setRemark("编辑操作权限");
            result.add(editOp);
            
            // 数字类型操作
            PermitRefOtftRespDTO numberReadOp = new PermitRefOtftRespDTO();
            numberReadOp.setId(3L);
            numberReadOp.setOperationTypeCode("READ");
            numberReadOp.setOperationTypeName("查看");
            numberReadOp.setFieldTypeCode("NUMBER");
            numberReadOp.setSort(1);
            numberReadOp.setRemark("数字查看操作权限");
            result.add(numberReadOp);
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取权限参考操作类型列表失败", e);
            return CommonResult.<List<PermitRefOtftRespDTO>>error(500, "获取权限参考操作类型列表失败：" + e.getMessage());
        }
    }
}
