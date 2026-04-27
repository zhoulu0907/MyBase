package com.cmsr.onebase.module.flow.core.dal.mapper;

import com.mybatisflex.core.BaseMapper;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import org.apache.ibatis.annotations.Param;

/**
 * 映射层。
 *
 * @author HuangJie
 * @since 2025-11-25
 */
public interface FlowProcessMapper extends BaseMapper<FlowProcessDO> {

    /**
     * 根据 processId 查询流程定义 JSON
     *
     * @param processId 流程ID
     * @return 流程定义 JSON 字符串
     */
    String selectProcessDefinitionByProcessId(@Param("processId") Long processId);
}
