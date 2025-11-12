package com.cmsr.onebase.module.system.vo.corp;

import com.cmsr.onebase.module.system.vo.corpapprelation.AppAuthTimeReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CorpCombinedVo {
    @Schema(description = "企业基础数据")
    @NotNull(message = "企业基础数据对象不能为空")
    public CorpReqVO corpReqVO;

    @Schema(description = "企业管理员")
    @NotNull(message = "企业管理员不能为空")
    public CorpAdminReqVO corpAdminReqVO;

    @Schema(description = "授权应用")
    public List<AppAuthTimeReqVO> appAuthTimeReqVO;

}
