package com.cmsr.onebase.module.flow.api;

import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;

/**
 * @Author：huangjie
 * @Date：2025/9/19 10:57
 */
public interface FlowProcessExecApi {

    EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO entityTriggerReqDTO);

}
