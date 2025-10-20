// 2. 创建 enterprise 模块的 VO 对象
package com.cmsr.onebase.module.system.api.enterprise.dto;


import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业分页查询参数
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
public class EnterprisePageReqVO extends PageParam {

    @Schema(description = "企业名称", example = "阿里巴巴")
    private String enterpriseName;

    @Schema(description = "企业编号", example = "ALIBABA")
    private String enterpriseCode;

    @Schema(description = "行业类型", example = "1")
    private Integer industryType;

    @Schema(description = "状态", example = "1")
    private Integer status;
}
