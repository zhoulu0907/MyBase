package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.express.JdbcTypeConvertor;
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
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@Setter
@LiteflowComponent("dataQueryMultiple")
public class DataQueryMultipleNodeComponent extends NormalNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private DataMethodApiHelper dataMethodApiHelper;

    @Override
    public void process() throws Exception {
        log.info("DataQueryMultipleNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        Map<String, Object> nodeData = executeContext.getNodeData(this.getTag());
        // 转换成数据方法参数
        EntityFieldDataReqDTO reqDTO = dataMethodApiHelper.convert(nodeData, variableContext);
        reqDTO.setNum(MapUtils.getInteger(nodeData, "maxCount", 500));
        List<List<EntityFieldDataRespDTO>> fieldDataRespDTOSS = TenantUtils.executeIgnore(() -> dataMethodApi.getDataByCondition(reqDTO));
        if (CollectionUtils.isNotEmpty(fieldDataRespDTOSS)) {
            variableContext.putNodeVariables(this.getTag(), convert(fieldDataRespDTOSS));
        }
    }

    private List<Map<String, Object>> convert(List<List<EntityFieldDataRespDTO>> fieldDataRespDTOSS) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (List<EntityFieldDataRespDTO> fieldDataRespDTOS : fieldDataRespDTOSS) {
            Map<String, Object> map = new HashMap<>();
            for (EntityFieldDataRespDTO dto : fieldDataRespDTOS) {
                map.put(dto.getFieldName(), JdbcTypeConvertor.convert(dto.getJdbcType(), dto.getFieldValue()));
            }
            list.add(map);
        }
        return list;
    }

}
