package com.cmsr.onebase.framework.common.anyline.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BaseDTO
 * @Description 基础数据传输对象，包含所有DTO的通用字段
 * @Author mickey
 * @Date 2025/7/7 22:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;

    @JsonProperty("deleted_by")
    private Long deletedBy;

    @JsonProperty("version")
    private Integer version;

    @JsonProperty("created_time")
    private java.time.LocalDateTime createdTime;

    @JsonProperty("updated_time")
    private java.time.LocalDateTime updatedTime;

    @JsonProperty("deleted_time")
    private java.time.LocalDateTime deletedTime;
}
