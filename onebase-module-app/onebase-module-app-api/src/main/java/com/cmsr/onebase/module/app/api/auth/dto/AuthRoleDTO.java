package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

@Data
public class AuthRoleDTO {

    private Long id;

    private String roleCode;

    private String roleName;

    private Integer roleType;

    private String description;

}
