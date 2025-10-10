package com.cmsr.onebase.framework.data.base;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 基础实体对象
 *
 * @author liyang
 * @date 2025/10/10
 */
public interface BaseDOInterface {

    /**
     * 获取id
     *
     * @return {@link Long}
     */
    Long getId();
}
