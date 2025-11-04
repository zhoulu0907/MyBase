package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.module.flow.core.graph.FlowVersionUpdate;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author：huangjie
 * @Date：2025/11/3 13:34
 */
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class FlowVersionUpdateTest {

    @Autowired
    private FlowVersionUpdate flowVersionUpdate;

    @Test
    public void testUpdateApplicationVersion() {
        flowVersionUpdate.updateApplicationVersion(46699591748616192L);
    }
}
