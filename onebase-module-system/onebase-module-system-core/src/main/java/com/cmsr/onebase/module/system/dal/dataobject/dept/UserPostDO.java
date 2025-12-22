package com.cmsr.onebase.module.system.dal.dataobject.dept;

import com.cmsr.onebase.framework.data.base.BaseDO;
import lombok.Data;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;

@Table(value = "system_user_post")
@Data
public class UserPostDO extends BaseDO {
    public static final String USER_ID = "user_id";
    public static final String POST_ID = "post_id";

    @Column(value = USER_ID)
    private Long userId;
    @Column(value = POST_ID)
    private Long postId;

}
