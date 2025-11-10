package com.cmsr.onebase.module.bpm.core.dto.node.base;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户信息
 *
 * @author liyang
 * @date 2025-10-21
 */
@Data
public class UserDTO {

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    private String name;
}
