package com.cmsr.onebase.module.flow.service.exec;

import com.cmsr.onebase.module.flow.controller.app.exec.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.controller.app.exec.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.flow.controller.app.exec.vo.QueryFormTriggerRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/4 16:34
 */
public interface FlowProcessExecService {

    List<QueryFormTriggerRespVO> queryFormTrigger(Long pageId);

    FormTriggerRespVO triggerForm(FormTriggerReqVO reqVO);

}
