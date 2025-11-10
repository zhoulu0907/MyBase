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

    @Schema(description = "行业类型")
    private Long industryType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime beginCreateTime;

    @Schema(description = "结束时间")
    private LocalDateTime endCreateTime;

    @Schema(description = "是否查询当前用户创建的应用还是全部的应用")
    private Integer ownerTag;
}
