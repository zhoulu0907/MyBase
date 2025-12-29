package com.cmsr.onebase.module.app.runtime.vo.menu;

import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/30 12:37
 */
@Data
public class MenuPermissionVO {

    private OperationPermission operationPermission;

    private FieldPermission fieldPermission;

    private List<String> viewUuids;

}
