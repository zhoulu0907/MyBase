package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.MethodCodeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;

import java.util.List;
import java.util.Map;

@Schema(description = "记录上下文 DTO")
@Data
/**
 * 记录上下文 DTO
 *
 * <p>承载菜单、链路追踪、分页排序过滤与包含控制等上下文信息。</p>
 */
public class RecordContextDTO {
    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "链路追踪ID")
    private String traceId;

    @Schema(description = "是否包含子表")
    private Boolean containSubTable;

    @Schema(description = "是否包含关系")
    private Boolean containRelation;

    @Schema(description = "方法编码（可选）")
    private MethodCodeEnum methodCode;

    @Schema(description = "操作类型：CREATE/UPDATE/DELETE/GET/GET_PAGE")
    private MetadataDataMethodOpEnum operationType;

    @Schema(description = "页码")
    private Integer pageNo;

    @Schema(description = "分页大小")
    private Integer pageSize;

    @Schema(description = "多字段排序规则")
    private List<SortRuleDTO> sortBy;

    @Schema(description = "过滤条件")
    private Map<String, Object> filters;

    @Schema(description = "权限上下文")
    private MetadataPermissionContext permissionContext;

    @Schema(description = "登录用户上下文")
    private LoginUserCtx loginUserCtx;

}
