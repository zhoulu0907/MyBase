package com.cmsr.onebase.module.bpm.runtime.service.operator;

import com.cmsr.onebase.module.bpm.runtime.vo.BpmOperatorRecordRespVO;

import java.util.List;

/**
 * 流程操作服务接口
 *
 * @author liyang
 * @date 2025-11-18
 */
public interface BpmOperatorRecordService {
    /**
     * 获取流程实例的操作记录
     *
     * @param instanceId 流程实例id
     * @return 操作记录 list
     *
     */
    List<BpmOperatorRecordRespVO.OperatorRecord> getOperatorRecord(Long instanceId);
}
