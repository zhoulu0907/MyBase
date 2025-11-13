package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.module.flow.core.graph.FlowCacheClient;
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
public class FlowCacheClientTest {

    @Autowired
    private FlowCacheClient flowCacheClient;

    @Test
    public void testUpdateApplicationVersion() {
        flowCacheClient.applicationUpdate(120906250090807296L);
    }
}
