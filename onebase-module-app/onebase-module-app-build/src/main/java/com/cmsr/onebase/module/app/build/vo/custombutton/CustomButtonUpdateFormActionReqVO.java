package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "自定义按钮-修改当前表单动作配置")
public class CustomButtonUpdateFormActionReqVO {

    @Schema(description = "打开方式：DIALOG 弹窗、DRAWER 抽屉、NEW_TAB 新页签")
    private String openMode;

    @Schema(description = "提交成功提示文案；为空默认“操作成功”")
    private String submitSuccessText;

    @Valid
    @Schema(description = "字段配置。fieldMode=EDIT 表示待用户手动修改；fieldMode=AUTO 表示点击后按规则自动更新")
    private List<CustomButtonUpdateFieldReqVO> updateFields;
}
