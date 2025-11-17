package com.cmsr.onebase.module.app.core.vo.app;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/17 14:50
 */
@Data
public class AppUserPhotoDTO {

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 用户id
     */
    private String id;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickName;
}
