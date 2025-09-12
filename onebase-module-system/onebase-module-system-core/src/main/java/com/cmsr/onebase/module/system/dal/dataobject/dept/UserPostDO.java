package com.cmsr.onebase.module.system.dal.dataobject.dept;

import com.cmsr.onebase.framework.data.base.BaseDO;
import lombok.Data;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Table(name = "system_user_post")
@Data
public class UserPostDO extends BaseDO {
    public static final String USER_ID = "user_id";
    public static final String POST_ID = "post_id";

    @Column(name = USER_ID)
    private Long userId;
    @Column(name = POST_ID)
    private Long postId;

}
