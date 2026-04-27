package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "自定义按钮-可用条件配置")
public class CustomButtonAvailableConditionReqVO {

    @Valid
    @Schema(description = "条件二维数组。外层数组元素之间为 OR，内层数组元素之间为 AND")
    private List<List<CustomButtonConditionItemReqVO>> valueRules;
}
