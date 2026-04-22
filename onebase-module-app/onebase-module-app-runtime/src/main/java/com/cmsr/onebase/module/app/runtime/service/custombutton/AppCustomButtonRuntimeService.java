package com.cmsr.onebase.module.app.runtime.service.custombutton;

import com.cmsr.onebase.module.app.runtime.vo.custombutton.*;

import java.util.List;

public interface AppCustomButtonRuntimeService {

    List<RuntimeCustomButtonRespVO> listAvailable(RuntimeCustomButtonListReqVO reqVO);

    RuntimeCustomButtonExecuteRespVO execute(RuntimeCustomButtonExecuteReqVO reqVO);

    RuntimeCustomButtonBatchExecuteRespVO batchExecute(RuntimeCustomButtonBatchExecuteReqVO reqVO);

    RuntimeCustomButtonExecLogRespVO getExecLog(Long execLogId);
}
