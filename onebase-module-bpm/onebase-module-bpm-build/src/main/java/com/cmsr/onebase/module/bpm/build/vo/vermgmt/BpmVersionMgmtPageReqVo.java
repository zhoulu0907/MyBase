package com.cmsr.onebase.module.bpm.build.vo.vermgmt;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 获取流程版本管理列表VO
 *
 * @author liyang
 * @date 2025-10-20
 */
@Data
public class BpmVersionMgmtPageReqVo extends PageParam {

    @Schema(description = "表单ID", required = true, example = "113771690916872193")
    @NotNull(message = "表单ID不能为空")
    private Long menuUuid;

    @Schema(description = "流程版本备注")
    private String bpmVersionAlias;

    @Schema(description = "流程版本状态")
    private String bpmVersionStatus;

    @Schema(description = "排序方式：update_time-按更新时间排序, create_time-按创建时间排序",
            example = "update_time", defaultValue = "update_time")
    @NotBlank(message = "排序方式不能为空")
    private String sortType;
}
