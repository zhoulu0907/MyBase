package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 数据方法 Service 核心接口 - 只提供基础数据操作，不依赖VO
 *
 * @author bty418
 * @date 2025-09-10
 */
public interface MetadataDataMethodService {

    // ========== 查询方法（供build模块使用） ==========

    /**
     * 查询启用的数据方法列表
     *
     * @param entityId 实体ID
     * @param methodType 方法类型（可选）
     * @param keyword 关键词（可选）
     * @return 数据方法DO列表
     */
    List<Map<String, Object>> getEnabledDataMethodList(String entityId, String methodType, String keyword);

    /**
     * 根据编码获取数据方法详情
     *
     * @param entityId 实体ID
     * @param methodCode 方法编码
     * @return 数据方法DO信息
     */
    Map<String, Object> getDataMethodByCode(String entityId, String methodCode);

    // ========== 动态数据操作方法（供runtime模块使用） ==========

    /**
     * 新增单条数据
     *
     * @param entityId 实体ID
     * @param data 数据内容
     * @param methodCode 方法编码（可选）
     * @return 新增后的数据（包含主键等）
     */
    Map<String, Object> createData(String entityId, Map<String, Object> data, String methodCode);

    /**
     * 更新单条数据
     *
     * @param entityId 实体ID
     * @param id 数据ID
     * @param data 数据内容
     * @param methodCode 方法编码（可选）
     * @return 更新后的数据
     */
    Map<String, Object> updateData(String entityId, Object id, Map<String, Object> data, String methodCode);

    /**
     * 删除单条数据
     *
     * @param entityId 实体ID
     * @param id 数据ID
     * @param methodCode 方法编码（可选）
     * @return 删除是否成功
     */
    Boolean deleteData(String entityId, Object id, String methodCode);

    /**
     * 根据ID查询数据详情
     *
     * @param entityId 实体ID
     * @param id 数据ID
     * @param methodCode 方法编码（可选）
     * @return 数据详情Map，包含实体信息和字段类型信息
     */
    Map<String, Object> getData(String entityId, Object id, String methodCode);

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
    PageResult<Map<String, Object>> getDataPage(String entityId, Integer pageNo, Integer pageSize, 
                                               String sortField, String sortDirection, 
                                               Map<String, Object> filters, String methodCode);

}