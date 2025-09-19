package com.cmsr.onebase.module.flow.core.flow.component;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@LiteflowComponent("startDateField")
public class StartDateFieldNodeComponent extends NormalNodeComponent {


    @Override
    public void process() throws Exception {
        log.info("StartDateFieldNodeComponent process");
    }

}
