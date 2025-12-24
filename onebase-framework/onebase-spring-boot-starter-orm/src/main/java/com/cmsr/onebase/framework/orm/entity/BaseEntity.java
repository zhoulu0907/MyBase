package com.cmsr.onebase.framework.orm.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字段：ID、创建人、创建时间、更新人、更新时间、删除标识、乐观锁；
 * <p>
 * 适用场景：系统表、字典表等忽略租户、应用权限管控的相关表单；
 */
@Data
public class BaseEntity {

    @Deprecated // 请flex的TableDef类
    public static final String COL_ID = "id";
    @Deprecated // 请flex的TableDef类
    public static final String CREATE_TIME = "create_time";
    @Deprecated // 请flex的TableDef类
    public static final String UPDATE_TIME = "update_time";
    @Deprecated // 请flex的TableDef类
    public static final String CREATOR = "creator";
    @Deprecated // 请flex的TableDef类
    public static final String UPDATER = "updater";
    @Deprecated // 请flex的TableDef类
    public static final String DELETED = "deleted";


    @Id(comment = "主键ID")
    private Long id;

    @Column(value = "creator", comment = "创建人")
    private Long creator;

    @Column(value = "create_time", comment = "创建时间")
    private LocalDateTime createTime;

    @Column(value = "updater", comment = "更新人")
    private Long updater;

    @Column(value = "update_time", comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(value = "deleted", comment = "删除标识")
    private Long deleted;

}
