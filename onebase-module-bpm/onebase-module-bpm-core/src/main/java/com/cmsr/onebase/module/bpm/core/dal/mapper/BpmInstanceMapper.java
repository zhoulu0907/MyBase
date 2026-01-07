package com.cmsr.onebase.module.bpm.core.dal.mapper;


import com.cmsr.onebase.module.bpm.core.dto.BpmInstanceDTO;
import com.cmsr.onebase.module.bpm.core.vo.BpmFormDataPageReqVO;

import java.util.List;


/**
 * 任务信息Mapper接口
 *
 * @author may
 * @date 2024-03-02
 */
public interface BpmInstanceMapper {

    /**
     * 获取待办信息
     *
     * @param queryVO        条件
     * @param userId        当前用户ID
     * @return 结果
     */
    List<BpmInstanceDTO> getFormDataPage(BpmFormDataPageReqVO queryVO, Long userId);
}
