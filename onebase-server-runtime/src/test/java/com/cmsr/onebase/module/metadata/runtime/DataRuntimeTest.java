package com.cmsr.onebase.module.metadata.runtime;

import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataUpdateReqVO;
import com.cmsr.onebase.module.metadata.runtime.service.datamethod.RuntimeDataService;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Setter
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class DataRuntimeTest {


    @Autowired
    private RuntimeDataService runtimeDataService;

    @Test
    public void authUpdateData() {
        System.out.println(1);

        DynamicDataUpdateReqVO reqVO = new DynamicDataUpdateReqVO();
        reqVO.setMenuId(113431890281824256L);
        reqVO.setEntityId(113429588179353600L);
        reqVO.setId(1);
        reqVO.setData(null);

        DynamicDataRespVO dynamicDataRespVO = runtimeDataService.updateData(reqVO);

        System.out.println(dynamicDataRespVO);
    }
}
