package com.cmsr.onebase.module.bpm.api.dto.node.base;

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
    private Long userId;

    /**
     * 用户姓名
     */
    private String name;
}
