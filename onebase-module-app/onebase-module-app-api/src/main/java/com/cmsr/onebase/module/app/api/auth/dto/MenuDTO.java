package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/25 15:52
 */
@Data
public class MenuDTO {

    private Long id;

    private Long applicationId;

    private Long entityId;

    private String menuCode;

}
