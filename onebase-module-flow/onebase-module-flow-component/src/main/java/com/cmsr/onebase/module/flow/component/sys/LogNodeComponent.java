package com.cmsr.onebase.module.flow.component.sys;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@LiteflowComponent("log")
public class LogNodeComponent extends NodeComponent {

    @Override
    public void process() throws Exception {
        log.info("LogNodeComponent process: {}", this.getTag());
    }

}
