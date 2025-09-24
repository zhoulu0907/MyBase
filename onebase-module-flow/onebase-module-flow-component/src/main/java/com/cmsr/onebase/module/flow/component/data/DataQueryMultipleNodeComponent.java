package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    public void process() throws Exception {
        log.info("DataQueryMultipleNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        Map<String, Object> nodeData = executeContext.getNodeData(this.getTag());
        EntityFieldDataReqDTO reqDTO = DataMethodApiUtils.convert(nodeData);
        dataMethodApi.getDataByCondition(reqDTO);
    }

}
