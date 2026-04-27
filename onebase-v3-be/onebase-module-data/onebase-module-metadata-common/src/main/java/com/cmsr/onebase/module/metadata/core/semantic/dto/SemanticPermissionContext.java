package com.cmsr.onebase.module.metadata.core.semantic.dto;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "权限上下文 DTO")
public class SemanticPermissionContext {

    @Schema(description = "操作权限")
    private OperationPermission operationPermission;

    @Schema(description = "数据权限")
    private DataPermission dataPermission;

    @Schema(description = "字段权限")
    private FieldPermission fieldPermission;
}
