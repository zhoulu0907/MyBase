package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.app.core.dal.database.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dto.auth.RoleMemberDTO;
import com.cmsr.onebase.module.app.core.impl.auth.AppAuthRoleUserImpl;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:33
 */
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class RepositoryTest {

    @Autowired
    private AppSqlQueryRepository appSqlQueryRepository;

    @Autowired
    private AppAuthRoleUserImpl appAuthRoleUser;

    @Test
    void test() {
        TenantContextHolder.setIgnore(true);
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(10);
        PageResult<RoleMemberDTO> result = appSqlQueryRepository.findRoleMembers(37775560235057154L, null, "dept", pageParam);
        System.out.println(result);
        System.out.println(result.getList());
        System.out.println(result.getTotal());
    }

    @Test
    void test2() {
        TenantContextHolder.setIgnore(true);
        List<Long> ids = appSqlQueryRepository.findDeptHierarchyByUserId(85546007599284224L);

    }

    @Test
    void test3() {
        TenantContextHolder.setIgnore(true);
        List<Long> ids = appSqlQueryRepository.findAllUserIdsByDeptIds(889796964974590L,1);
        System.out.println(ids);
    }
}
