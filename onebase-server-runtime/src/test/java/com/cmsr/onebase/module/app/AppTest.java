package com.cmsr.onebase.module.app;

import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.impl.auth.AppAuthSecurityApiImpl;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthSecurityDataGroupProvider;
import com.cmsr.onebase.module.app.core.vo.menu.MenuListRespVO;
import com.cmsr.onebase.module.app.runtime.service.menu.RuntimeAppMenuService;
import com.cmsr.onebase.module.app.runtime.vo.menu.MenuPermissionVO;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;


/**
 * @Author：huangjie
 * @Date：2025/9/1 12:11
 */
@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class AppTest {


    @Autowired
    private AppAuthDataGroupRepository appAuthDataGroupRepository;

    @Autowired
    private AppAuthSecurityDataGroupProvider appAuthSecurityDataGroupProvider;

    @Autowired
    private AppAuthSecurityApiImpl appAuthSecurityApi;

    @Autowired
    private RuntimeAppMenuService runtimeAppMenuService;

    @Autowired
    private AppApplicationApi appApplicationApi;

    @Test
    public void testSimple() throws IOException {
        TenantContextHolder.setIgnore(true);
        RTSecurityContext.mockLoginUser(3386012505007460352L, 46699591748616192L);
        List<MenuListRespVO> menuListRespVOS = runtimeAppMenuService.listApplicationMenu();
        System.out.println(menuListRespVOS);
    }

    @Test
    public void testSimple2() throws IOException {
        TenantContextHolder.setIgnore(true);
        RTSecurityContext.mockLoginUser(3386012505007460352L, 46699591748616192L);
        MenuPermissionVO menuPermission = runtimeAppMenuService.getMenuPermission(47012574606491648L);
        System.out.println(menuPermission);
    }

    @Test
    public void testGraph() throws IOException {
        TenantContextHolder.setIgnore(true);
        RTSecurityContext.mockLoginUser(3386012505007460352L, 46699591748616192L);
        MenuPermissionVO menuPermissionVO = runtimeAppMenuService.getMenuPermission(95847916169691136L);
        System.out.println(menuPermissionVO);
    }

    @Test
    public void testGraph2() throws IOException {
        TenantContextHolder.setIgnore(true);
        OperationPermission operationPermission = appAuthSecurityApi.getMenuOperationPermission(3386012505007460352L, 46699591748616192L, 47012574606491648L);
        System.out.println(operationPermission);
    }

    @Test
    public void testGraph3() throws IOException {
        TenantContextHolder.setIgnore(true);
        FieldPermission menuFieldPermission = appAuthSecurityApi.getMenuFieldPermission(3386012505007460352L, 46699591748616192L, 47012574606491648L);
        System.out.println(menuFieldPermission);
    }

    @Test
    public void testGraph4() throws IOException {
        TenantContextHolder.setIgnore(true);
        RTSecurityContext.mockLoginUser(3386012505007460352L, 46699591748616192L);
        FieldPermission fieldPermission = RTSecurityContext.getMenuFieldPermission(47012574606491648L);
        System.out.println(fieldPermission);
    }

    @Test
    public void testGraph5() throws IOException {
        ApplicationDTO result = appApplicationApi.findAppApplicationById(181672186277396480L);
        System.out.println(result);
    }

}
