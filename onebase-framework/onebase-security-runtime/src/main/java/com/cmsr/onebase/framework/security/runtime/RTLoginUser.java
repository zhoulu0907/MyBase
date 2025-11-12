package com.cmsr.onebase.framework.security.runtime;

import com.cmsr.onebase.framework.security.core.LoginUser;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/17 12:31
 */
@Data
public class RTLoginUser extends LoginUser {
    /**
     * 应用ID
     */
    private Long applicationId;
}
