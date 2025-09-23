package com.cmsr.onebase.module.metadata.api.datamethod;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.cmsr.onebase.module.metadata.api.datamethod.dto.DeleteDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "数据方法 sdk")
public interface DataMethodApi {

    /**
     * 根据条件查询实体字段数据
     *
     * @param reqDTO 查询请求参数
     * @return 字段数据列表
     */
    @Operation(summary = "根据条件查询实体字段数据")
    List<EntityFieldDataRespDTO> getDataByCondition(@Valid @RequestBody EntityFieldDataReqDTO reqDTO);
    
    /**
     * 根据条件删除实体字段数据
     *
     * @param reqDTO 删除请求参数
     * @return 删除成功的数量
     */
    Integer deleteDataByCondition(@Valid @RequestBody DeleteDataReqDTO reqDTO);

    /**
     * 插入实体字段数据
     *
     * @param reqDTO 插入请求参数
     * @return 插入成功的数量
     */
    Integer insertData(@Valid @RequestBody InsertDataReqDTO reqDTO);
}
