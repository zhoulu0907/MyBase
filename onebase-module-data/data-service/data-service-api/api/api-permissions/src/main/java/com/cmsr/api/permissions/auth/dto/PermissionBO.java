package com.cmsr.api.permissions.auth.dto;

import com.cmsr.api.permissions.auth.vo.PermissionItem;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class PermissionBO extends PermissionItem {

    private Long resourceId;
}
