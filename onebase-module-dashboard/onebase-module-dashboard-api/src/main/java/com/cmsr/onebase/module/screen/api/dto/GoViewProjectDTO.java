package com.cmsr.onebase.module.screen.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author fc
 * @since 2023-04-30
 */
@Data
public class GoViewProjectDTO implements Serializable {

    private Long id;

    private String projectName;

    private Integer state;

    private String createTime;

    private String createUserId;

    private Integer isDelete;

    private String indexImage;

    private String remarks;

    private Long tenantId;

    private Long appId;

}