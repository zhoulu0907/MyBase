package com.cmsr.onebase.module.dashboard.build.model;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
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
@Table("t_goview_project")
@Data
public class GoviewProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "uuid")
    private String id;

    private String projectName;

    private Integer state;

    @Column(onInsertValue = "now()")
    private String createTime;

    private String createUserId;

    private Integer isDelete;

    private String indexImage;

    private String remarks;

    private Long tenantId;

    private Long appId;

}