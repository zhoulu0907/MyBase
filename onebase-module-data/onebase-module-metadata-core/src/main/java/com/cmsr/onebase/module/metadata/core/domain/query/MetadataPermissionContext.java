package com.cmsr.onebase.module.metadata.core.domain.query;

import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import lombok.Data;

/**
 * 访问元数据的用户权限上下文
 */
@Data
public class MetadataPermissionContext {

    /**
     * 操作权限
     */
    private OperationPermission operationPermission;

    /**
     * 数据权限
     */
    private DataPermission dataPermission;

    /**
     * 字段权限
     */
    private FieldPermission fieldPermission;
}
