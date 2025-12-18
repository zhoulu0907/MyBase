package com.cmsr.onebase.module.screen.build.v2.model;

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
@Table("t_goview_project_data")
@Data
public class GoviewProjectData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id(keyType = KeyType.Generator, value = "uuid")
    private String id;

    private String projectId;

    @Column(onInsertValue = "now()")
    private String createTime;

    private String createUserId;

    private String content;


}
