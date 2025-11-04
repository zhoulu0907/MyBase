package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppSqlQueryRepository;
import com.cmsr.onebase.module.app.core.dto.auth.UserMemberDTO;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:33
 */
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class RepositoryTest {

    @Autowired
    private AppSqlQueryRepository appSqlQueryRepository;

    @Test
    void test() {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(10);
        PageResult<UserMemberDTO> result = appSqlQueryRepository.findUserMemberDTOByRoleId(6943244133695489L, "管理员", pageParam);
        System.out.println(result);
        System.out.println(result.getList());
        System.out.println(result.getTotal());
    }
}
