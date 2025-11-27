package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author：huangjie
 * @Date：2025/11/26 16:42
 */
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class AppAuthSecurityApiTest {

    @Autowired
    private AppAuthSecurityApi appAuthSecurityApi;

    @Test
    void checkMenuEntity() {
        boolean applicationAdmin = appAuthSecurityApi.isApplicationAdmin(1L, 153750139950727168L);
        System.out.println(applicationAdmin);
    }

    @Test
    void getMenuOperationPermission() {
        OperationPermission menuOperationPermission = appAuthSecurityApi.getMenuOperationPermission(1L, 153750139950727168L, 153803071128567808L);
        System.out.println(menuOperationPermission);
    }
}
