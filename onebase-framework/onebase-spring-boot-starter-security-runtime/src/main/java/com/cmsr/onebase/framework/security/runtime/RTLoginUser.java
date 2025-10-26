package com.cmsr.onebase.framework.security.runtime;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/17 12:31
 */
@Data
public class RTLoginUser {

    private String token;

    private Long userId;

    private Long applicationId;

}
