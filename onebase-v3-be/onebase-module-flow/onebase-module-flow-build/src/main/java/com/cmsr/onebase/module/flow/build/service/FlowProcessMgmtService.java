package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.vo.PageFlowProcessReqVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 流程管理服务接口
 */
public interface FlowProcessMgmtService {

    /**
     * 分页查询流程
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    PageResult<FlowProcessVO> pageList(PageFlowProcessReqVO reqVO);

    /**
     * 获取流程详情
     *
     * @param id 流程ID
     * @return 流程详情
     */
    FlowProcessVO getDetail(Long id);

    /**
     * 创建流程
     *
     * @param reqVO 创建参数
     * @return 流程ID
     */
    Long create(CreateFlowProcessReqVO reqVO);

    /**
     * 更新流程
     *
     * @param reqVO 更新参数
     */
    void update(UpdateFlowProcessReqVO reqVO);

    /**
     * 更新流程定义
     *
     * @param reqVO 更新参数
     */
    void updateProcessDefinition(@Valid UpdateProcessDefinitionReqVO reqVO);

    /**
     * 重命名流程
     *
     * @param reqVO 重命名参数
     */
    void renameFlowProcess(RenameFlowProcessReqVO reqVO);

    /**
     * 启用流程
     *
     * @param id 流程ID
     */
    void enableFlowProcess(Long id);

    /**
     * 关闭流程
     *
     * @param id 流程ID
     */
    void disableFlowProcess(Long id);

    /**
     * 删除流程
     *
     * @param id 流程ID
     */
    void delete(Long id);

    /**
     * 批量删除流程
     *
     * @param ids 流程ID列表
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据 processId 查询流程定义 JSON
     *
     * @param processId 流程ID
     * @return 流程定义 JSON 字符串
     */
    String getProcessDefinitionByProcessId(Long processId);
}
