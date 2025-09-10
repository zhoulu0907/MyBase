package com.cmsr.onebase.module.metadata.api.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限 - 关联查询 API 实现
 *
 * @author bty418
 * @date 2025-08-14
 */
@RestController
@Validated
@Slf4j
public class MetadataPermitApiImpl implements MetadataPermitApi {

    @Resource
    private AnylineService<?> anylineService;

    @Override
    public CommonResult<List<PermitRefOtftRespDTO>> getPermitRefOtftList() {
        String sql = "select mpo.id,  mcft.field_type_code, mcft.field_type_name , mvt.validation_code, mvt.validation_name " +
                "from metadata_permit_ref_otft mpo , metadata_component_field_type mcft , metadata_validation_type mvt " +
                "where mpo.field_type_id = mcft.id and mpo.validation_type_id = mvt.id order by mpo.sort_order";

        log.info("执行关联查询 SQL: {}", sql);
        DataSet ds = anylineService.querys(sql);
        List<PermitRefOtftRespDTO> list = new ArrayList<>();
        for (DataRow row : ds) {
            PermitRefOtftRespDTO dto = new PermitRefOtftRespDTO();
            dto.setId(row.getLong("id"));
            dto.setFieldTypeCode(row.getString("field_type_code"));
            dto.setFieldTypeName(row.getString("field_type_name"));
            dto.setValidationCode(row.getString("validation_code"));
            dto.setValidationName(row.getString("validation_name"));
            list.add(dto);
        }
        log.info("关联查询返回记录数: {}", list.size());
        return CommonResult.success(list);
    }
}


