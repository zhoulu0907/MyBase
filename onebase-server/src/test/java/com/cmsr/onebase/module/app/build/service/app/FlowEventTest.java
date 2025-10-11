package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.module.flow.core.event.FlowEventPublisher;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author：huangjie
 * @Date：2025/10/10 16:18
 */
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class FlowEventTest {

    @Autowired
    private FlowEventPublisher flowEventPublisher;

    @Test
    public void test() {
        flowEventPublisher.publishProcessUpdate(84076905441918976L);
    }
}
