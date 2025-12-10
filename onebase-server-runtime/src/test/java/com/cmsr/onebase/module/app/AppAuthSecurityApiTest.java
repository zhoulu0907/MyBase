package com.cmsr.onebase.module.app;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author：huangjie
 * @Date：2025/12/10 11:04
 */
@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class AppAuthSecurityApiTest {

    @Autowired
    private AppAuthSecurityApi appAuthSecurityApi;

    @Test
    public void testIsApplicationAdmin() {
        boolean applicationAdmin = appAuthSecurityApi.isApplicationAdmin(155019577667616800L, 173020283873034240L);
        System.out.println(applicationAdmin);
    }

    @Test
    public void testCheckMenuEntity() {
        boolean menuEntity = appAuthSecurityApi.checkMenuEntity(173020283873034240L, 173059110377881600L, "019b01b5-8f41-7bb5-a8e7-830d21380031");
        System.out.println(menuEntity);
    }

    @Test
    public void testGetMenuOperationPermission() {
        ApplicationManager.setVersionTag(0L);
        OperationPermission menuOperationPermission = appAuthSecurityApi.getMenuOperationPermission(155019577667616800L, 173020283873034240L, 173059110377881600L);
        System.out.println(menuOperationPermission);
    }

    @Test
    public void testGetMenuOperationPermission2() {
        ApplicationManager.setVersionTag(0L);
        OperationPermission menuOperationPermission = appAuthSecurityApi.getMenuOperationPermission(155019577667616768L, 173020283873034240L, 173059110377881600L);
        System.out.println(menuOperationPermission);
    }

    @Test
    public void testGetMenuDataPermission2() {
        ApplicationManager.setVersionTag(0L);
        DataPermission menuDataPermission = appAuthSecurityApi.getMenuDataPermission(155019577667616768L, 173020283873034240L, 173059110377881600L);
        System.out.println(menuDataPermission);
    }

    @Test
    public void testGetMenuFieldPermission2() {
        ApplicationManager.setVersionTag(0L);
        FieldPermission menuFieldPermission = appAuthSecurityApi.getMenuFieldPermission(155019577667616768L, 173020283873034240L, 173059110377881600L);
        System.out.println(menuFieldPermission);
    }

}
