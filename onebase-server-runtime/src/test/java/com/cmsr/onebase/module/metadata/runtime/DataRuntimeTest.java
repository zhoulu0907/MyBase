package com.cmsr.onebase.module.metadata.runtime;

import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataDeleteReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.DynamicDataUpdateReqVO;
import com.cmsr.onebase.module.metadata.runtime.service.datamethod.RuntimeDataService;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

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
        reqVO.setId(116480234742382592L);
        Map<Long, Object> data = new HashMap<>(){
            {
                put(113430189474775040L, "aaa");
                put(113440583295631360L, "x");
                put(113440583295631361L, "aassssss");
            }
        };
        reqVO.setData(data);
        TenantContextHolder.setIgnore(true);
        RTSecurityContext.mockLoginUser(116683300797415424L, 113428849444978688L);
        DynamicDataRespVO dynamicDataRespVO = runtimeDataService.updateData(reqVO);
        System.out.println(dynamicDataRespVO);
    }


    @Test
    public void authDeleteData() {
        System.out.println(1);
        DynamicDataDeleteReqVO reqVO = new DynamicDataDeleteReqVO();
        reqVO.setMenuId(113431890281824256L);
        reqVO.setEntityId(113429588179353600L);
        reqVO.setId(116480234742382592L);
//        Map<Long, Object> data = new HashMap<>(){
//            {
//                put(113430189474775040L, "aaa");
//                put(113440583295631360L, "x");
//                put(113440583295631361L, "aassssss");
//            }
//        };
//        reqVO.setData(data);
        TenantContextHolder.setIgnore(true);
        RTSecurityContext.mockLoginUser(116683300797415424L, 113428849444978688L);
        boolean dynamicDataRespVO = runtimeDataService.deleteData(reqVO);
        System.out.println(dynamicDataRespVO);
    }


}
