package com.cmsr.onebase.module.bpm.core.dal.mapper;

import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.dto.BpmCcRecordDTO;
import com.cmsr.onebase.module.bpm.core.vo.BpmCcTaskPageReqVO;
import com.mybatisflex.core.BaseMapper;

import java.util.List;

/**
 * 流程抄送记录表 映射层。
 *
 * @author liyang
 * @since 2025-11-28
 */
public interface BpmFlowCcRecordMapper extends BaseMapper<BpmFlowCcRecordDO> {
    /**
     * 获取抄送分页列表
     *
     * @param reqVO
     * @param userId
     * @return
     */
    List<BpmCcRecordDTO> getCcPage(BpmCcTaskPageReqVO reqVO, String userId);
}
