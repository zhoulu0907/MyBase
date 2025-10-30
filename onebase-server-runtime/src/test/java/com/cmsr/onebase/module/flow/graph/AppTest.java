package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.MenuPermission;
import com.cmsr.onebase.module.app.core.biz.auth.AppAuthSecurityApiImpl;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.provider.auth.AppAuthDataGroupProvider;
import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.job.FlowJobMessage;
import com.cmsr.onebase.module.flow.core.job.FlowJobMessageHandler;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


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
    private AppAuthDataGroupProvider appAuthDataGroupProvider;

    @Autowired
    private AppAuthSecurityApiImpl appAuthSecurityApi;

    @Test
    public void testSimple() throws IOException {
        List<AuthDataGroupDO> authDataGroupDOS = appAuthDataGroupRepository.findByAppIdAndRoleIdsAndMenuId(
                46699591748616192L,
                Set.of(46699591748616193L, 46699591748616194L, 104446011218624512L),
                46699591748616194L);
        System.out.println(authDataGroupDOS);

        List<DataPermissionGroup> dataGroups = appAuthDataGroupProvider.findDataGroups(46699591748616192L,
                Set.of(46699591748616193L, 46699591748616194L, 104446011218624512L),
                46699591748616194L);
        System.out.println(dataGroups);
    }

    @Test
    public void testGraph() throws IOException {
        DataPermission menuDataPermission = appAuthSecurityApi.getMenuDataPermission(3386012505007460352L, 46699591748616192L, 95847916169691136L);
        System.out.println(menuDataPermission);
    }

    @Test
    public void testGraph2() throws IOException {
        TenantContextHolder.setIgnore(true);
        MenuPermission menuPermission = appAuthSecurityApi.getMenuPermission(3386012505007460352L, 46699591748616192L, 47012574606491648L);
        System.out.println(menuPermission);
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
        RTSecurityContext.mockLoginUser(3386012505007460352L,46699591748616192L);
        FieldPermission fieldPermission = RTSecurityContext.getMenuFieldPermission(47012574606491648L);
        System.out.println(fieldPermission);
    }

}
