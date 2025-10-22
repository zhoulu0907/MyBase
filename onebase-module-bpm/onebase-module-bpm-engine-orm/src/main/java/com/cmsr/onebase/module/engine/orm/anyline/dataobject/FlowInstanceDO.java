package com.cmsr.onebase.module.engine.orm.anyline.dataobject;

import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import lombok.Data;

@Data
public class FlowInstanceDO extends FlowInstance {
    /**
     * 当前节点处理人
     */
    private String currentNodeHandler;
}
