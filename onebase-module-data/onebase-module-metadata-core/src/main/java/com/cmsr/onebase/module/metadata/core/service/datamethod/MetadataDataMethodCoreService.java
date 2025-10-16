package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;

import java.util.Map;
import java.util.List;

/**
 * 数据方法 Service 核心接口 - 只提供基础数据操作，不依赖VO
 *
 * @author bty418
 * @date 2025-09-10
 */
public interface MetadataDataMethodCoreService {

    // ========== 动态数据操作方法（供runtime模块使用） ==========

    /**
     * 新增单条数据
     *
     * @param entityId 实体ID
     * @param data 数据内容
     * @param methodCode 方法编码（可选）
     * @return 新增后的数据（包含主键等）
     */
    Map<String, Object> createData(Long entityId, Map<String, Object> data, String methodCode);

    /**
     * 更新单条数据
     *
     * @param entityId 实体ID
     * @param id 数据ID
     * @param data 数据内容
     * @param methodCode 方法编码（可选）
     * @return 更新后的数据
     */
    Map<String, Object> updateData(Long entityId, Object id, Map<String, Object> data, String methodCode);

    /**
     * 删除单条数据
     *
     * @param entityId 实体ID
     * @param id 数据ID
     * @param methodCode 方法编码（可选）
     * @return 删除是否成功
     */
    Boolean deleteData(Long entityId, Object id, String methodCode);

    /**
     * 根据ID查询数据详情
     *
     * @param entityId 实体ID
     * @param id 数据ID
     * @param methodCode 方法编码（可选）
     * @return 数据详情Map，包含实体信息和字段类型信息
     */
    Map<String, Object> getData(Long entityId, Object id, String methodCode);

    /**
     * 分页查询数据列表
     *
     * @param entityId 实体ID
     * @param pageNo 页码
     * @param pageSize 页大小
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param filters 过滤条件
     * @param methodCode 方法编码（可选）
     * @return 分页数据结果，每个数据项都是Map，包含实体信息和字段类型信息
     */
    PageResult<Map<String, Object>> getDataPage(Long entityId, Integer pageNo, Integer pageSize,
                                               String sortField, String sortDirection,
                                               Map<String, Object> filters, String methodCode);

    /**
     * OR 复合条件分页查询（单SQL实现 (group1) OR (group2) ... ）
     *
     * @param entityId 实体ID
     * @param pageNo 页码
     * @param pageSize 页大小
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param orConditionGroups OR 条件组列表；每组内部为 AND 关系；结构同 buildComplexFilters 输出：key 任意，value 为包含 fieldName/operator/value 的 Map
     * @param methodCode 方法编码（可选）
     * @return 分页数据
     */
    PageResult<Map<String, Object>> getDataPageOr(Long entityId, Integer pageNo, Integer pageSize,
                                                  String sortField, String sortDirection,
                                                  List<Map<String,Object>> orConditionGroups,
                                                  String methodCode);

}
