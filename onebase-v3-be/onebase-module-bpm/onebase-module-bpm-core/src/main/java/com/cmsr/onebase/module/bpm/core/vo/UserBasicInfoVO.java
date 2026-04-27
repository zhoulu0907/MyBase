package com.cmsr.onebase.module.bpm.core.vo;

import lombok.Data;

/**
 * 用户基本信息VO
 *
 * @author liyang
 * @date 2025-11-05
 */
@Data
public class UserBasicInfoVO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户头像
     */
    private String avatar;
}
