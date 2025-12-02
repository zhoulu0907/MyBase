package com.cmsr.onebase.module.app.build.service.etl;

import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.etl.build.service.datasource.EtlDatasourceService;
import com.cmsr.onebase.server.OneBaseServerApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author：huangjie
 * @Date：2025/11/11 13:36
 */
@Setter
@SpringBootTest(classes = OneBaseServerApplication.class)
public class EtlDatasourceServiceTest {

    @Autowired
    private EtlDatasourceService etlDatasourceService;

    @Test
    public void test() {
        TenantContextHolder.setIgnore(true);
        etlDatasourceService.executeMetadataCollectJob(126770168960581632L);
    }
}
