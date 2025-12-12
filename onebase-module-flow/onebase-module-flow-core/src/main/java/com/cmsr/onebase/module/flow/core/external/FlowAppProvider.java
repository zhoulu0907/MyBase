package com.cmsr.onebase.module.flow.core.external;

/**
 * @Author：huangjie
 * @Date：2025/12/8 21:21
 */
public interface FlowAppProvider {

    Long findPageIdByAppIdAndPageUuid(Long applicationId, String menuUuid);

    String findTableUuidByAppIdAndPageUuid(Long applicationId, String menuUuid);

}
