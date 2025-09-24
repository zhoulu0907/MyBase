package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.express.JdbcTypeConvertor;
import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
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

    @Override
    public void process() throws Exception {
        log.info("DataQueryNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        Map<String, Object> nodeData = executeContext.getNodeData(this.getTag());
        EntityFieldDataReqDTO reqDTO = DataMethodApiUtils.convert(nodeData);
        List<EntityFieldDataRespDTO> fieldDataRespDTOS = dataMethodApi.getDataByCondition(reqDTO);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        variableContext.getNodeVariables().put(this.getTag(), convert(fieldDataRespDTOS));
    }

    private Map<String, Object> convert(List<EntityFieldDataRespDTO> fieldDataRespDTOS) {
        Map<String, Object> map = new HashMap<>();
        for (EntityFieldDataRespDTO fieldDataRespDTO : fieldDataRespDTOS) {
            map.put(fieldDataRespDTO.getFieldName(), JdbcTypeConvertor.convert(fieldDataRespDTO.getJdbcType(), fieldDataRespDTO.getFieldValue()));
        }
        return map;
    }
}
