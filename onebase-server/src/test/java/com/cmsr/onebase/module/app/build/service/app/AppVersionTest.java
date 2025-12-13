package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.module.app.build.service.version.AppVersionService;
import com.cmsr.onebase.module.app.build.vo.version.VersionOnlineReq;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @Author：huangjie
 * @Date：2025/11/26 16:42
 */
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class AppVersionTest {

    @Autowired
    private AppVersionService appVersionService;

    @Test
    public void createApplicationVersion() {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(155019577667616800L);
        SecurityFrameworkUtils.setLoginUser(loginUser, new MockHttpServletRequest());
        //
        TenantContextHolder.setTenantId(153935442021842944L);
        ApplicationManager.setApplicationId(173020283873034240L);
        ApplicationManager.setVersionTag(0L);
        //
        VersionOnlineReq createReqVO = new VersionOnlineReq();
        createReqVO.setApplicationId(173020283873034240L);
        createReqVO.setVersionName("测试版本2");
        createReqVO.setVersionNumber("1.0.1");
        createReqVO.setVersionDescription("测试版本2");
        appVersionService.onlineApplication(createReqVO);

    }


}
