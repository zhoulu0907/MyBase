package com.cmsr.onebase.framework.common.anyline.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName BaseDO
 * @Description 基础数据对象，包含所有实体的通用字段
 * @Author mickey
 * @Date 2025/7/7 08:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BaseDO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT PRIMARY KEY", comment = "ID")
    private Long id;

    @Column(name = "created_by", columnDefinition = "BIGINT", comment = "创建人ID")
    private Long createdBy;

    @Column(name = "updated_by", columnDefinition = "BIGINT", comment = "更新人ID")
    private Long updatedBy;

    @Column(name = "deleted_by", columnDefinition = "BIGINT", comment = "删除人ID")
    private Long deletedBy;

    @Column(name = "version", columnDefinition = "INT", comment = "乐观锁版本")
    private Integer version;

    @Column(name = "created_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", comment = "创建时间")
    private java.time.LocalDateTime createdTime;

    @Column(name = "updated_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", comment = "更新时间")
    private java.time.LocalDateTime updatedTime;

    @Column(name = "deleted_time", columnDefinition = "TIMESTAMP DEFAULT NULL", comment = "删除时间")
    private java.time.LocalDateTime deletedTime;
}
