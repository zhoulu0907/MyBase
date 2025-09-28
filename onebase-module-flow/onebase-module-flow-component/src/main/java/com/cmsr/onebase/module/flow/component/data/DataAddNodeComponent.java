package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.ValueProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
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
@LiteflowComponent("dataAdd")
public class DataAddNodeComponent extends NormalNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private DataMethodApiHelper dataMethodApiHelper;

    @Autowired
    private ValueProvider valueProvider;

    @Override
    public void process() throws Exception {
        log.info("DataAddNodeComponent process - 开始处理节点数据添加操作");
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        Map<String, Object> nodeData = executeContext.getNodeData(this.getTag());
        // 参数校验
        if (MapUtils.isEmpty(nodeData)) {
            log.warn("DataAddNodeComponent process - 节点数据为空，跳过处理");
            return;
        }
        
        Long entityId = MapUtils.getLong(nodeData, "mainEntityId");
        if (entityId == null) {
            log.error("DataAddNodeComponent process - 实体ID不能为空");
            throw new IllegalArgumentException("实体ID不能为空");
        }
        
        boolean batchType = MapUtils.getBooleanValue(nodeData, "batchType", false);
        log.debug("DataAddNodeComponent process - 操作类型：{}", batchType ? "批量添加" : "单条添加");
        
        // 构建请求数据
        List<Map<Long, Object>> reqData = buildRequestData(nodeData, variableContext, batchType);
        
        if (CollectionUtils.isEmpty(reqData)) {
            log.warn("DataAddNodeComponent process - 构建的请求数据为空，跳过处理");
            return;
        }
        
        // 执行数据添加操作
        InsertDataReqDTO reqDTO = new InsertDataReqDTO();
        reqDTO.setEntityId(entityId);
        reqDTO.setData(reqData);
        
        log.debug("DataAddNodeComponent process - 准备执行数据插入，实体ID: {}, 数据条数: {}", entityId, reqData.size());
        
        try {
            List<List<EntityFieldDataRespDTO>> respDTOSS = TenantUtils.executeIgnore(() -> dataMethodApi.insertData(reqDTO));
            // 处理响应结果
            processResponse(respDTOSS, variableContext, batchType);
            log.info("DataAddNodeComponent process - 数据添加操作完成，影响行数: {}", respDTOSS != null ? respDTOSS.size() : 0);
        } catch (Exception e) {
            log.error("DataAddNodeComponent process - 数据添加操作失败，实体ID: {}", entityId, e);
            throw e; // 重新抛出异常，保持原有行为
        }
    }
    
    /**
     * 构建请求数据
     */
    private List<Map<Long, Object>> buildRequestData(Map<String, Object> nodeData, VariableContext variableContext, boolean batchType) {
        List<Map<String, Object>> fields = (List<Map<String, Object>>) MapUtils.getObject(nodeData, "fields");
        if (CollectionUtils.isEmpty(fields)) {
            log.warn("DataAddNodeComponent buildRequestData - 字段配置为空");
            return new ArrayList<>();
        }
        if (batchType) {
            return buildBatchRequestData(nodeData, variableContext, fields);
        } else {
            return buildSingleRequestData(variableContext, fields);
        }
    }
    
    /**
     * 构建批量请求数据
     */
    private List<Map<Long, Object>> buildBatchRequestData(Map<String, Object> nodeData, VariableContext variableContext, List<Map<String, Object>> fields) {
        String dataNodeId = MapUtils.getString(nodeData, "dataNodeId");
        int dataSize = variableContext.getVariableSizeByTag(dataNodeId);
        log.debug("DataAddNodeComponent buildBatchRequestData - 批量处理数据量: {}", dataSize);
        
        List<Map<Long, Object>> reqData = new ArrayList<>(dataSize);
        
        for (int i = 0; i < dataSize; i++) {
            Map<Long, Object> data = new HashMap<>();
            for (Map<String, Object> field : fields) {
                processField(field, data, i, variableContext);
            }
            reqData.add(data);
        }
        return reqData;
    }
    
    /**
     * 构建单条请求数据
     */
    private List<Map<Long, Object>> buildSingleRequestData(VariableContext variableContext, List<Map<String, Object>> fields) {
        Map<Long, Object> data = new HashMap<>();
        fields.forEach(field -> processField(field, data, -1, variableContext));
        return List.of(data);
    }
    
    /**
     * 处理单个字段
     */
    private void processField(Map<String, Object> field, Map<Long, Object> data, int index, VariableContext variableContext) {
        Long fieldId = MapUtils.getLong(field, "fieldId");
        String operatorType = MapUtils.getString(field, "operatorType");
        Object fieldValue = MapUtils.getObject(field, "value");
        if (fieldId == null) {
            log.warn("DataAddNodeComponent processField - 字段ID为空，跳过处理");
            return;
        }
        // 根据是否为批量操作调用不同的转换方法
        if (index >= 0) {
            fieldValue = valueProvider.convertValue(index, operatorType, fieldValue, variableContext);
        } else {
            fieldValue = valueProvider.convertValue(operatorType, fieldValue, variableContext);
        }
        data.put(fieldId, fieldValue);
    }
    
    /**
     * 处理响应结果
     */
    private void processResponse(List<List<EntityFieldDataRespDTO>> respDTOSS, VariableContext variableContext, boolean batchType) {
        if (CollectionUtils.isEmpty(respDTOSS)) {
            log.warn("DataAddNodeComponent processResponse - 响应结果为空");
            return;
        }
        if (batchType) {
            variableContext.putNodeVariables(this.getTag(), dataMethodApiHelper.convertToListMap(respDTOSS));
        } else {
            variableContext.putNodeVariables(this.getTag(), dataMethodApiHelper.convertToMap(respDTOSS.get(0)));
        }
        log.debug("DataAddNodeComponent processResponse - 响应结果已添加到变量上下文中");
    }

}
