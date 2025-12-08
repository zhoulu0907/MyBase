package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerReqVO;
import com.cmsr.onebase.module.flow.runtime.vo.FormTriggerRespVO;
import com.cmsr.onebase.module.flow.runtime.vo.QueryFormTriggerRespVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/4 16:34
 */
public interface FlowProcessExecService {

    List<QueryFormTriggerRespVO> queryFormTrigger( String pageUuid);

    FormTriggerRespVO triggerForm(FormTriggerReqVO reqVO);

}
