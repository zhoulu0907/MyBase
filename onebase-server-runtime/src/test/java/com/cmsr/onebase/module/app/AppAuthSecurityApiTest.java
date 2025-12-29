package com.cmsr.onebase.module.app;

import com.cmsr.onebase.framework.common.enums.RunModeEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;
import com.cmsr.onebase.module.app.runtime.service.menu.RuntimeAppMenuService;
import com.cmsr.onebase.module.app.runtime.service.menu.RuntimeAppMenuServiceImpl;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuPermissionVO;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/12/10 11:04
 */
@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class AppAuthSecurityApiTest {

    @Autowired
    private RuntimeAppMenuService runtimeAppMenuService;

    @Autowired
    private AppAuthSecurityApi appAuthSecurityApi;

    private static final Long APPLICATION_ID = 168411371638652928L;

    private static final Long USER_ID = 194493282173878272L;

    private static final Long TENANT_ID = 168334577623138304l;

    private static final Long MENU_ID = 195089440814203300L;

    @BeforeEach
    public void beforeEach() {
        ApplicationManager.setApplicationId(APPLICATION_ID);
        ApplicationManager.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        TenantContextHolder.setTenantId(TENANT_ID);

        LoginUser loginUser = new LoginUser();
        loginUser.setId(USER_ID);
        loginUser.setTenantId(TENANT_ID);
        loginUser.setRunMode(RunModeEnum.RUNTIME.getValue());
        loginUser.setUserType(UserTypeEnum.THIRD.getValue());
        SecurityFrameworkUtils.setLoginUser(loginUser, new MockHttpServletRequest());
        //appAuthSecurityApi.cleanAuthCache(USER_ID, APPLICATION_ID);
    }


    @Test
    public void testHasApplicationPermission() {
        boolean b = appAuthSecurityApi.hasApplicationPermission(USER_ID, APPLICATION_ID);
        System.out.println(b);
    }


    @Test
    public void testIsApplicationAdmin() {
        boolean applicationAdmin = appAuthSecurityApi.isApplicationAdmin(USER_ID, APPLICATION_ID);
        System.out.println(applicationAdmin);
    }

    @Test
    public void testGetVisibleMenuIds() {
        List<Long> menuIds = appAuthSecurityApi.getVisibleMenuIds(USER_ID, APPLICATION_ID);
        System.out.println(menuIds);
    }

    @Test
    public void testListApplicationMenu() {
        List<MenuListRespVO> menuListRespVOS = runtimeAppMenuService.listApplicationMenu();
        System.out.println(menuListRespVOS);
    }

    @Test
    public void testGetMenuPermission() {
        MenuPermissionVO menuListRespVO = runtimeAppMenuService.getMenuPermission(MENU_ID);
        System.out.println(menuListRespVO);
    }

    @Test
    public void testGetMenuOperationPermission() {
        OperationPermission operationPermission = appAuthSecurityApi.getMenuOperationPermission(USER_ID, APPLICATION_ID, MENU_ID);
        System.out.println(operationPermission);
    }

    @Test
    public void testGetMenuDataPermission() {
        DataPermission dataPermission = appAuthSecurityApi.getMenuDataPermission(USER_ID, APPLICATION_ID, MENU_ID);
        System.out.println(dataPermission);
    }

    @Test
    public void testGetMenuFieldPermission() {
        FieldPermission fieldPermission = appAuthSecurityApi.getMenuFieldPermission(USER_ID, APPLICATION_ID, MENU_ID);
        System.out.println(fieldPermission);
    }


    @Test
    public void testCheckMenuEntity() {
        boolean menuEntity = appAuthSecurityApi.checkMenuEntity(APPLICATION_ID, MENU_ID, "019b01b5-8f41-7bb5-a8e7-830d21380031");
        System.out.println(menuEntity);
    }


}
