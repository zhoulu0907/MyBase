package com.cmsr.onebase.module.app.core.dto.auth;

import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:15
 */
@Data
public class RoleMemberDTO {

    public static final String MEMBER_TYPE_USER = "user";

    public static final String MEMBER_TYPE_DEPT = "dept";

    private Long id;

    @Column("member_id")
    private Long memberId;

    @Column("member_name")
    private String memberName;

    @Column("member_type")
    private String memberType;

    @Column("is_include_child")
    private Integer isIncludeChild;

    @Column("dept_name")
    private String deptName;

}
