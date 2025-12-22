package com.cmsr.onebase.module.app;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.module.app.core.dal.database.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthRoleMapper;
import com.cmsr.onebase.module.app.core.enums.menu.MenuTypeEnum;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:33
 */
@Slf4j
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class RepositoryTest {

    @Autowired
    private AppSqlQueryRepository appSqlQueryRepository;

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Autowired
    private AppAuthRoleMapper appAuthRoleMapper;

    @Autowired
    private AppAuthDataGroupRepository appAuthDataGroupRepository;

    @Autowired
    private AppApplicationRepository appApplicationRepository;

    @Autowired
    private AppAuthPermissionRepository appAuthPermissionRepository;

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Autowired
    private AppPageRepository appPageRepository;

    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @BeforeAll
    public static void before() {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(155019577667616800L);
        SecurityFrameworkUtils.setLoginUser(loginUser, new MockHttpServletRequest());
        //
        TenantContextHolder.setTenantId(153935442021842944L);
        ApplicationManager.setApplicationId(173020283873034240L);
        ApplicationManager.setVersionTag(0L);
        //
    }

    @Test
    void test1() {
        List<AppAuthRoleDO> authRoleDOS = appAuthRoleRepository.findByUserIdAndApplicationId(155019577667616800L, 173020283873034240L);
        log.info("authRoleDOS:{}", authRoleDOS);
    }

    @Test
    void test2() {
        List<AppResourcePageDO> pages = appPageRepository.findPagesByMenuId(175770196824981504L);
        log.info("pages:{}", pages);
    }

    @Test
    void test3() {
        List<AppMenuDO> result = appMenuRepository.findVisibleByAppIdAndType(173020283873034240L, Set.of(MenuTypeEnum.PAGE.getValue(), MenuTypeEnum.GROUP.getValue()));
        log.info("result:{}", result);
    }

    @Test
    void test4() {
        List<AppMenuDO> result = appMenuRepository.findByApplicationId(173020283873034240L);
        log.info("authRoleDOS:{}", result);
    }

    @Test
    void test5() {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(163858465771585536L);
        SecurityFrameworkUtils.setLoginUser(loginUser, new MockHttpServletRequest());
        ApplicationManager.setApplicationId(162919276682641408L);
        ApplicationManager.setVersionTag(0L);
        TenantContextHolder.setTenantId(1L);
        //
        flowProcessTimeRepository.updateJobStatusByAppId("success", 1234567890L);
    }

}
