package com.cmsr.onebase.framework.common.security.dto;

/**
 * @Author：huangjie
 * @Date：2025/10/17 12:31
 */
public class RuntimeLoginUser extends LoginUser {
    /**
     * 应用ID
     */
    private Long applicationId;

    /**
     * 不推荐从RuntimeLoginUser中获取applicationId；
     * 建议使用{@link com.cmsr.onebase.framework.common.security.ApplicationManager}中的getApplicationId()方法
     *
     * @return
     */
    @Deprecated
    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
