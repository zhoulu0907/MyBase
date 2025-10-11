package com.cmsr.onebase.module.flow.component.sys;

import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：huangjie
 * @Date：2025/9/16 10:58
 */
@Slf4j
@LiteflowComponent("noop")
public class NoOpNodeComponent extends SkippableNodeComponent {
    @Override
    public void process() throws Exception {
        log.info("NoOpNodeComponent process");
    }

}
