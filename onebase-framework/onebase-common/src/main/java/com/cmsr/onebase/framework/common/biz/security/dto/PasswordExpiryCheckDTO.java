package com.cmsr.onebase.framework.common.biz.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码有效期检查结果DTO
 *
 * @author chengyuansen
 * @date 2025-11-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordExpiryCheckDTO {

    /**
     * 检查结果类型
     * - expired: 密码已过期
     * - valid: 密码未过期
     */
    private String type;

    /**
     * 密码过期天数（仅当type=expired时有值）
     * 例如：5 表示密码已过期5天
     */
    private Integer daysExpired;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 密码年龄（天数）
     */
    private Integer passwordAge;

    /**
     * 密码有效期配置（天数）
     */
    private Integer expiryDays;

}
