package com.cmsr.onebase.module.app.api.app.dto;

import lombok.Data;

@Data
public class UserPhotoDTO {
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
