package com.cmsr.onebase.module.flow.component.logic;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;

/**
 * @Author：huangjie
 * @Date：2025/9/26 14:01
 */
@LiteflowComponent("ifCase")
public class IfCaseNodeComponent extends NodeBooleanComponent {

    @Override
    public boolean processBoolean() throws Exception {
        return false;
    }

}
