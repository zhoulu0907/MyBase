package com.cmsr.onebase.module.metadata.runtime.semantic.service.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.SubEntityVo;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.SemanticDynamicDataService;
import com.cmsr.onebase.module.metadata.runtime.semantic.executor.SemanticCreateExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.executor.SemanticUpdateExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.executor.SemanticDeleteExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.executor.SemanticDetailExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.executor.SemanticPageExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticPageBodyVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
/**
 * 语义动态数据服务实现
 *
 * <p>
 * 将语义化请求体解析为 RecordDTO，再装配为运行态数据方法请求VO，
 * 委派
 * {@link com.cmsr.onebase.module.metadata.runtime.service.datamethod.RuntimeDataService}
 * 执行。
 * </p>
 */
public class SemanticDynamicDataServiceImpl implements SemanticDynamicDataService {

    @Resource
    private SemanticCreateExecutor createExecutor;

    @Resource
    private SemanticUpdateExecutor updateExecutor;

    @Resource
    private SemanticDeleteExecutor deleteExecutor;

    @Resource
    private SemanticDetailExecutor detailExecutor;

    @Resource
    private SemanticPageExecutor pageExecutor;

    @Override
    /**
     * 创建数据：解析合并语义体并执行业务创建
     * 
     * @param tableName 表名
     * @param menuId    菜单ID
     * @param body      合并请求体
     * @param traceId   链路追踪ID
     * @return 创建后的响应
     */
    public Map<String, Object> create(String tableName, Long menuId, SemanticMergeBodyVO body, String traceId) {
        Map<String, Object> result = createExecutor.execute(tableName, menuId, traceId, body);
        return result;
    }

    @Override
    /**
     * 更新数据：解析合并语义体并执行业务更新
     * 
     * @param tableName 表名
     * @param menuId    菜单ID
     * @param body      合并请求体，包含主键
     * @param traceId   链路追踪ID
     * @return 更新后的响应
     */
    public DynamicDataRespVO update(String tableName, Long menuId, SemanticMergeBodyVO body, String traceId) {
        // Map<String, Object> result = updateExecutor.execute(tableName, menuId, traceId, body);
        // return convertToDynamicDataRespVO(result);
        return null;
    }

    @Override
    /**
     * 删除数据：解析目标语义体并执行业务删除
     * 
     * @param tableName 表名
     * @param menuId    菜单ID
     * @param body      目标请求体，包含主键
     * @param traceId   链路追踪ID
     * @return 删除成功返回被删除数据ID，失败返回 null
     */
    public Long remove(String tableName, Long menuId, SemanticTargetBodyVO body, String traceId) {
        // return deleteExecutor.execute(tableName, menuId, traceId, body) ? null : null;
        return null;
    }

    @Override
    /**
     * 查询详情：解析目标语义体并执行业务查询
     * 
     * @param tableName 表名
     * @param menuId    菜单ID
     * @param body      目标请求体，包含主键
     * @param traceId   链路追踪ID
     * @return 详情响应
     */
    public DynamicDataRespVO detail(String tableName, Long menuId, SemanticTargetBodyVO body, String traceId) {
        // Map<String, Object> result = detailExecutor.execute(tableName, menuId, traceId, body);
        // return convertToDynamicDataRespVO(result);
        return null;
    }

    @Override
    /**
     * 分页查询：解析分页语义体并执行业务分页
     * 
     * @param tableName 表名
     * @param menuId    菜单ID
     * @param body      分页请求体
     * @param traceId   链路追踪ID
     * @return 分页响应
     */
    public PageResult<DynamicDataRespVO> page(String tableName, Long menuId, SemanticPageBodyVO body, String traceId) {
        PageResult<Map<String, Object>> page = pageExecutor.execute(tableName, menuId, traceId, body);
        List<DynamicDataRespVO> list = page.getList().stream()
                .map(this::convertToDynamicDataRespVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal());
    }

    private DynamicDataRespVO convertToDynamicDataRespVO(Map<String, Object> data) {
        DynamicDataRespVO respVO = new DynamicDataRespVO();
        Object eid = data.get("entityId");
        if (eid instanceof String) {
            try {
                respVO.setEntityId(Long.valueOf((String) eid));
            } catch (NumberFormatException ex) {
                respVO.setEntityId(null);
            }
        } else if (eid instanceof Number) {
            respVO.setEntityId(((Number) eid).longValue());
        }
        respVO.setEntityName((String) data.get("entityName"));
        respVO.setData((Map<String, Object>) data.get("data"));
        respVO.setFieldType((Map<String, String>) data.get("fieldType"));
        respVO.setSubEntities((List<SubEntityVo>) data.get("subEntities"));
        return respVO;
    }
}
