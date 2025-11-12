// 3. 创建 enterprise 模块的 VO 对象
package com.cmsr.onebase.module.system.vo.corp;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 企业保存请求参数
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
public class CorpSaveReqVO extends PageParam {

    @Schema(description = "企业主键ID")
    private Long id;

    @NotBlank(message = "企业名称不能为空")
    @Schema(description = "企业名称", example = "")
    private String corpName;

    @NotBlank(message = "企业编码不能为空")
    @Schema(description = "企业编码", example = "")
    private String corpCode;

    @NotNull(message = "行业类型不能为空")
    @Schema(description = "行业类型", example = "")
    private Long industryType;

    @NotBlank(message = "企业地址")
    @Schema(description = "企业地址", example = "")
    private String address;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态", example = "1")
    private Integer status;

    @NotNull(message = "用户上限不能为空")
    @Schema(description = "用户上限", example = "100")
    private Integer userCount;

}
