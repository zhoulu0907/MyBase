package com.cmsr.onebase.module.metadata.runtime.semantic.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataRespVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticPageBodyVO;

/**
 * 语义动态数据服务接口
 *
 * <p>负责将语义化请求体转换为运行态数据方法所需的请求对象，
 * 并调用底层 RuntimeDataService 执行业务操作。</p>
 */
public interface SemanticDynamicDataService {

    /**
     * 创建数据
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 合并请求体
     * @param traceId 链路追踪ID
     * @return 创建后的响应
     */
    DynamicDataRespVO create(String entityCode, Long menuId, SemanticMergeBodyVO body, String traceId);

    /**
     * 更新数据
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 合并请求体，包含主键
     * @param traceId 链路追踪ID
     * @return 更新后的响应
     */
    DynamicDataRespVO update(String entityCode, Long menuId, SemanticMergeBodyVO body, String traceId);

    /**
     * 删除数据
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 目标请求体，包含待删除主键
     * @param traceId 链路追踪ID
     * @return 删除成功返回被删除数据ID，失败返回 null
     */
    Long remove(String entityCode, Long menuId, SemanticTargetBodyVO body, String traceId);

    /**
     * 查询详情
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 目标请求体，包含主键及包含控制
     * @param traceId 链路追踪ID
     * @return 详情响应
     */
    DynamicDataRespVO detail(String entityCode, Long menuId, SemanticTargetBodyVO body, String traceId);

    /**
     * 分页查询
     * @param entityCode 实体编码
     * @param menuId 菜单ID
     * @param body 分页请求体，包含分页/排序/过滤
     * @param traceId 链路追踪ID
     * @return 分页响应
     */
    PageResult<DynamicDataRespVO> page(String entityCode, Long menuId, SemanticPageBodyVO body, String traceId);
}
