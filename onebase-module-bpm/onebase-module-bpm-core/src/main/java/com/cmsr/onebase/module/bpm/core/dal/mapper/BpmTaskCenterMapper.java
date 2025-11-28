package com.cmsr.onebase.module.bpm.core.dal.mapper;


import com.cmsr.onebase.module.bpm.core.dto.BpmDoneTaskDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmMyInstanceDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmTodoTaskDTO;
import com.cmsr.onebase.module.bpm.core.vo.BpmDoneTaskPageReqVO;
import com.cmsr.onebase.module.bpm.core.vo.BpmMyCreatedPageReqVO;
import com.cmsr.onebase.module.bpm.core.vo.BpmTodoTaskPageReqVO;

import java.util.List;


/**
 * 任务信息Mapper接口
 *
 * @author may
 * @date 2024-03-02
 */
public interface BpmTaskCenterMapper {

    /**
     * 获取待办信息
     *
     * @param queryVO        条件
     * @param userId        当前用户ID
     * @return 结果
     */
    List<BpmTodoTaskDTO> getTodoTaskPage(BpmTodoTaskPageReqVO queryVO, String userId);

    /**
     * 获取已办
     *
     * @param queryVO        条件
     * @param userId        当前用户ID
     * @return 结果
     */
    List<BpmDoneTaskDTO> getDoneTaskPage(BpmDoneTaskPageReqVO queryVO, String userId);

    /**
     * 获取我的发起流程
     *
     * @param queryVO        条件
     * @param userId        当前用户ID
     * @return 结果
     */
    List<BpmMyInstanceDTO> getMyCreatePage(BpmMyCreatedPageReqVO queryVO, Long userId);
}
