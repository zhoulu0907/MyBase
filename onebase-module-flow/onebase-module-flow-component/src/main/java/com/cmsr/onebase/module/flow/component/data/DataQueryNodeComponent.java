package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@Setter
@LiteflowComponent("dataQuery")
public class DataQueryNodeComponent extends NormalNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private DataMethodApiHelper dataMethodApiHelper;


    @Override
    public void process() throws Exception {
        log.info("DataQueryNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        Map<String, Object> nodeData = executeContext.getNodeData(this.getTag());
        // 转换成数据方法参数
        EntityFieldDataReqDTO reqDTO = dataMethodApiHelper.convertQueryReq(nodeData, variableContext);
        reqDTO.setNum(1);
        List<List<EntityFieldDataRespDTO>> fieldDataRespDTOS = TenantUtils.executeIgnore(() -> dataMethodApi.getDataByCondition(reqDTO));
        if (CollectionUtils.isNotEmpty(fieldDataRespDTOS)) {
            variableContext.putNodeVariables(this.getTag(), dataMethodApiHelper.convertToMap(fieldDataRespDTOS.get(0)));
        }
    }


}
