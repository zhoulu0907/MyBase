package com.cmsr.onebase.module.system.vo.corp;

import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CorpCombinedVo {
    @Schema(description = "企业基础数据")
    @NotNull(message = "企业基础数据对象不能为空")
    public CorpReqVO corpReqVO;

    @Schema(description = "企业管理员")
    @NotNull(message = "企业管理员不能为空")
    public CorpUserReqVO corpUserReqVO;

    @Schema(description = "应用id")
    @NotNull(message = "企业id list不能为空")
    public CorpAppRelationInertReqVO corpAppRelationInertReqVO;

}
