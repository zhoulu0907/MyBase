package com.cmsr.onebase.module.app.core.dto.auth;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:15
 */
@Data
public class UserMemberDTO {

    private Long Id;

    private Long memberId;

    private String memberName;

    private String memberType;

}
