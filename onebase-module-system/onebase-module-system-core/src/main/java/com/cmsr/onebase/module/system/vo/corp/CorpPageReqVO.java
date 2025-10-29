// 2. 创建 enterprise 模块的 VO 对象
package com.cmsr.onebase.module.system.vo.corp;


import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业分页查询参数
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
public class CorpPageReqVO extends PageParam {

    @Schema(description = "企业名称")
    private String corpName;

    @Schema(description = "企业编号")
    private String corpCode;

    @Schema(description = "行业类型")
    private Integer industryType;

    @Schema(description = "状态")
    private Integer status;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime beginTime;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
