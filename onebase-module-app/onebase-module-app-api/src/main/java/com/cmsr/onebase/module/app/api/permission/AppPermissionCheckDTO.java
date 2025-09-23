package com.cmsr.onebase.module.app.api.permission;

import lombok.Data;

@Data
public class AppPermissionCheckDTO {
    private Long   userId;
    private Long   appId;
    private String permission;
}
