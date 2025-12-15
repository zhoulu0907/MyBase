package com.cmsr.onebase.framework.common.event;

import lombok.Builder;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/12/12 15:46
 */
@Data
@Builder
public class AppUserLogoutEvent {

    private Long applicationId;

    private Long userId;

}
