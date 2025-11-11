package com.cmsr.onebase.module.app.core.dto.auth;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:15
 */
@Data
public class RoleMemberDTO {

    public static final String MEMBER_TYPE_USER = "user";

    public static final String MEMBER_TYPE_DEPT = "dept";

    private Long Id;

    private Long memberId;

    private String memberName;

    private String memberType;

    private Integer isIncludeChild;

    private String deptName;

}
