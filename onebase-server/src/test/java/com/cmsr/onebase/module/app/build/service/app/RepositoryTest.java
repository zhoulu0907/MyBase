package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.module.app.build.vo.app.ApplicationRespVO;
import com.cmsr.onebase.module.app.core.dal.database.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthDataGroupRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthRoleMapper;
import com.cmsr.onebase.module.app.core.impl.auth.AppAuthRoleUserImpl;
import com.cmsr.onebase.module.app.core.vo.app.AppUserPhotoDTO;
import com.cmsr.onebase.module.app.core.vo.app.ApplicationPageReqVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.server.OneBaseServerApplication;
import com.github.pagehelper.PageHelper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Map;
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
    private AppAuthRoleUserImpl appAuthRoleUser;

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Autowired
    private AppAuthRoleMapper appAuthRoleMapper;

    @Autowired
    private AppAuthDataGroupRepository appAuthDataGroupRepository;

    @Autowired
    private AppApplicationServiceImpl appApplicationService;

    @Autowired
    private AppApplicationRepository appApplicationRepository;

    @Autowired
    private AppAuthPermissionRepository appAuthPermissionRepository;

    private static final Long APP_ID = 89762669056458752L;


//    @Test
//    void test() {
//        TenantContextHolder.setIgnore(true);
//        PageParam pageParam = new PageParam();
//        pageParam.setPageNo(1);
//        pageParam.setPageSize(10);
//        PageResult<RoleMemberDTO> result = appSqlQueryRepository.findRoleMembers(37775560235057154L, null, "dept", pageParam);
//        System.out.println(result);
//        System.out.println(result.getList());
//        System.out.println(result.getTotal());
//    }

    @Test
    void test2() {
        TenantContextHolder.setIgnore(true);
        List<Long> ids = appSqlQueryRepository.findDeptHierarchyByUserId(85546007599284224L);

    }

    @Test
    void test3() {
        TenantContextHolder.setIgnore(true);
        List<Long> ids = appSqlQueryRepository.findAllUserIdsByDeptIds(889796964974590L, 1);
        System.out.println(ids);
    }

    @Test
    public void test6() {
        TenantContextHolder.setTenantId(1L);
//        var result = appMenuRepository.findPagesetPageByMenuId(89763253172011008L);
    }

    @Test
    public void test7() {
        com.github.pagehelper.Page<Object> page = PageHelper
                .startPage(1, 1)
                .doSelectPage(() -> appAuthRoleMapper.selectRoleMembers(46699591748616193L, ""));
        System.out.println(page);
    }

    @Test
    public void test8() {
        List<AppAuthDataGroupDO> appAuthDataGroupDOS = appAuthDataGroupRepository.findByAppIdAndRoleIdsAndMenuId(46699591748616192L, Set.of(104446011218624512L,
                140498533732220928L,
                46699591748616193L,
                46699591748616194L), 95847916169691136L);
        System.out.println(appAuthDataGroupDOS);
    }

    @Test
    public void test9() {
        List<AppUserPhotoDTO> result = appAuthRoleMapper.findUserPhotoList(List.of(46699591748616193L));
        System.out.println(result);
    }

    @Test
    public void test10() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        LoginUser loginUser = new LoginUser();
        loginUser.setId(46699591748616193L);
        SecurityFrameworkUtils.setLoginUser(loginUser, request);
        TenantContextHolder.setTenantId(153935442021842944L);

        ApplicationPageReqVO reqVO = new ApplicationPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setOwnerTag(0);
        PageResult<ApplicationRespVO> result = appApplicationService.getApplicationPage(reqVO);
        System.out.println(result);
    }

    @Test
    public void test11() {
        Map<Long, Integer> longIntegerMap = appApplicationRepository.countAppByTenantId();
        System.out.println(longIntegerMap);
    }

    @Test
    public void test12() {
        AuthPermissionReq reqVO = new AuthPermissionReq();
        reqVO.setApplicationId(155325722937688064L);
        reqVO.setRoleId(155325722937688067L);
        reqVO.setMenuId(155330550480928768L);
        AppAuthPermissionDO result = appAuthPermissionRepository.findByQuery(reqVO);
        System.out.println(result);
    }

    @Test
    public void testMoveToHis() {
        long versionTag = System.currentTimeMillis();
        //171622357911764992
        TenantContextHolder.setTenantId(153935442021842944L);
        LoginUser loginUser = new LoginUser();
        loginUser.setId(155019577667616800L);
        SecurityFrameworkUtils.setLoginUser(loginUser, new MockHttpServletRequest());
        flowProcessRepository.moveRuntimeToHistory(166945945974013952L, versionTag);
    }

    @Test
    public void testEditToRuntime() {
        //171622357911764992
        TenantContextHolder.setTenantId(153935442021842944L);
        LoginUser loginUser = new LoginUser();
        loginUser.setId(155019577667616800L);
        SecurityFrameworkUtils.setLoginUser(loginUser, new MockHttpServletRequest());
        flowProcessRepository.copyEditToRuntime(166945945974013952L);
    }
}
