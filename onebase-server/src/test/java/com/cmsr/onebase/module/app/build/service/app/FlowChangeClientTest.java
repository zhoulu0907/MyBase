package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.module.flow.core.handler.FlowChangeClient;
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
public class FlowChangeClientTest {

    @Autowired
    private FlowChangeClient flowChangeClient;

    @Test
    public void testUpdateApplicationVersion() {
        flowChangeClient.applicationUpdate(120906250090807296L);
    }
}
