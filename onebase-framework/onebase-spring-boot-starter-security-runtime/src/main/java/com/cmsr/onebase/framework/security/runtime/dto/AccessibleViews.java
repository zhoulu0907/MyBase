package com.cmsr.onebase.framework.security.runtime.dto;

import lombok.Data;

import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/27 10:50
 */
@Data
public class AccessibleViews {

    private boolean allAllowed;

    private boolean allDenied;

    private Set<Long> viewIds;
}
