package com.cmsr.onebase.module.infra.api.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录失败处理结果DTO
 *
 * @author chengyuansen
 * @date 2025-11-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginFailureResultDTO {

    /**
     * 是否已锁定
     */
    private Boolean locked;

    /**
     * 剩余尝试次数（未锁定时有值）
     */
    private Integer remainingAttempts;

    /**
     * 剩余锁定时间（秒）（已锁定时有值）
     */
    private Long remainingLockSeconds;

    /**
     * 提示信息
     */
    private String message;

}
