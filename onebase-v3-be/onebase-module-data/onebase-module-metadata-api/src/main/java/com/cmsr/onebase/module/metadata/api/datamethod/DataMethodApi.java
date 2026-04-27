package com.cmsr.onebase.module.metadata.api.datamethod;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.cmsr.onebase.module.metadata.api.datamethod.dto.DeleteDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.UpdateDataReqDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 数据方法API接口
 * 
 * @author bty418
 * @date 2025-10-23
 */
@Tag(name = "数据方法 API")
@Deprecated
public interface DataMethodApi {

    /**
     * 根据条件查询实体字段数据
     * 支持 andConditionDTO 和 conditionDTO 的组合查询
     * andConditionDTO 中的条件与 conditionDTO 中的每个条件组都是 AND 关系
     *
     * @param reqDTO 查询请求参数
     * @return 字段数据列表
     */
    @Operation(summary = "根据条件查询实体字段数据")
    List<List<EntityFieldDataRespDTO>> getDataByCondition(@Valid @RequestBody EntityFieldDataReqDTO reqDTO);
    
    /**
     * 根据条件删除实体字段数据
     *
     * @param reqDTO 删除请求参数
     * @return 删除成功的数量
     */
    Integer deleteDataByCondition(@Valid @RequestBody DeleteDataReqDTO reqDTO);

    /**
     * 插入实体字段数据
     * 该方法给外部模块调用，是被编辑态调用的
     *
     * @param reqDTO 插入请求参数
     * @return 返回插入的数据对象
     */
    List<List<EntityFieldDataRespDTO>> insertData(@Valid @RequestBody InsertDataReqDTO reqDTO);

    /**
     * 更新实体字段数据
     *
     * @param reqDTO 插入请求参数
     * @return 返回修改后的数据对象
     */
    List<List<EntityFieldDataRespDTO>> updateData(@Valid @RequestBody UpdateDataReqDTO reqDTO);
}

