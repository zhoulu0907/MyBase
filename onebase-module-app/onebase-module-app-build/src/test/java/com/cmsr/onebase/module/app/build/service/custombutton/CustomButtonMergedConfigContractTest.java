package com.cmsr.onebase.module.app.build.service.custombutton;

import com.cmsr.onebase.module.app.build.vo.custombutton.CustomButtonActionConfigReqVO;
import com.cmsr.onebase.module.app.build.vo.custombutton.CustomButtonUpdateFieldReqVO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonActionConfigDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonUpdateFieldDO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomButtonMergedConfigContractTest {

    @Test
    void mergedActionConfigCarriesFlowAndOpenPageFields() {
        CustomButtonActionConfigReqVO reqVO = new CustomButtonActionConfigReqVO();
        reqVO.setFlowProcessId(1001L);
        reqVO.setTargetType("INNER_PAGE");
        reqVO.setTargetPageUuid("page-test-detail");

        AppCustomButtonActionConfigDO configDO = new AppCustomButtonActionConfigDO();
        configDO.setFlowProcessId(reqVO.getFlowProcessId());
        configDO.setTargetType(reqVO.getTargetType());
        configDO.setTargetPageUuid(reqVO.getTargetPageUuid());

        assertEquals(1001L, configDO.getFlowProcessId());
        assertEquals("INNER_PAGE", configDO.getTargetType());
        assertEquals("page-test-detail", configDO.getTargetPageUuid());
    }

    @Test
    void mergedUpdateFieldCarriesEditableAndAutoUpdateFields() {
        CustomButtonUpdateFieldReqVO reqVO = new CustomButtonUpdateFieldReqVO();
        reqVO.setFieldMode("AUTO");
        reqVO.setFieldUuid("field-test-updater");
        reqVO.setValueType("CURRENT_USER");

        AppCustomButtonUpdateFieldDO fieldDO = new AppCustomButtonUpdateFieldDO();
        fieldDO.setFieldMode(reqVO.getFieldMode());
        fieldDO.setFieldUuid(reqVO.getFieldUuid());
        fieldDO.setValueType(reqVO.getValueType());

        assertEquals("AUTO", fieldDO.getFieldMode());
        assertEquals("field-test-updater", fieldDO.getFieldUuid());
        assertEquals("CURRENT_USER", fieldDO.getValueType());
    }
}
