package com.cmsr.onebase.module.app.build.service.app;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.app.build.service.version.AppVersionService;
import com.cmsr.onebase.module.app.build.vo.version.VersionCreateReqVO;
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
public class AppVersionTest {

    @Autowired
    private AppVersionService appVersionService;

    @Test
    public void createApplicationVersion() {
        TenantContextHolder.setTenantId(153935442021842944L);
        ApplicationManager.setApplicationId(173020283873034240L);
        ApplicationManager.setVersionTag(0L);
        //
        VersionCreateReqVO createReqVO = new VersionCreateReqVO();
        createReqVO.setApplicationId(173020283873034240L);
        createReqVO.setVersionName("测试版本1");
        createReqVO.setVersionNumber("1.0.0");
        createReqVO.setVersionDescription("测试版本1");
        appVersionService.createApplicationVersion(createReqVO);

    }


}
