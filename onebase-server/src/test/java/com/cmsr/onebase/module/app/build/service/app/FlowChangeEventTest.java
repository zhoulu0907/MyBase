package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.flow.core.event.FlowChangeEventPublisher;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/10 16:18
 */
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class FlowChangeEventTest {

    @Autowired
    private FlowChangeEventPublisher flowChangeEventPublisher;

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Test
    public void test() {
        flowChangeEventPublisher.publishApplicationUpdate(84076905441918976L);
    }

    @Test
    public void test2() {
        List<AuthRoleDO> list = appAuthRoleRepository.findByApplicationIdAndUserId(6943244133695488L, 114L);
        System.out.println(list);
    }
}
