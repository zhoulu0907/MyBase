package com.cmsr.onebase.framework.ds;

import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.framework.ds.client.DolphinschedulerClientStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DolphinschedulerClientTest {

    @Autowired
    private DolphinSchedulerClient client;

    private DolphinschedulerClientStub clientStub;

    @BeforeEach
    public void init() {
    }

    @Test
    public void testTaskCodeGenerate() {

    }

}
