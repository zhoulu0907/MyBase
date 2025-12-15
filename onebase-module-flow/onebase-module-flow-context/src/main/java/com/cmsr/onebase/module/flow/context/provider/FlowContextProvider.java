package com.cmsr.onebase.module.flow.context.provider;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;

/**
 * @Author：huangjie
 * @Date：2025/10/11 21:33
 */

public interface FlowContextProvider {

    void storeExecuteContext(String executionUuid, ExecuteContext executeContext);

    void storeVariableContext(String executionUuid, VariableContext variableContext);

    ExecuteContext restoreExecuteContext(String executionUuid);

    VariableContext restoreVariableContext(String executionUuid);

}
