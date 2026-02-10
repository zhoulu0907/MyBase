package com.cmsr.onebase.module.flow.core.external;

import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/12/9 9:20
 */
@Slf4j
@Component
public class FlowAppProviderImpl implements FlowAppProvider {

    @Autowired
    private AppResourceApi appResourceApi;


    @Override
    public Long findPageIdByAppIdAndPageUuid(Long applicationId, String menuUuid) {
        if (menuUuid == null || menuUuid.isEmpty()) {
            log.warn("查询页面ID时，pageUuid为空，applicationId: {}", applicationId);
            return null;
        }
        PageRespDTO respDTO = appResourceApi.findPageByPageUuid(applicationId, menuUuid);
        if (respDTO == null) {
            log.warn("根据pageUuid查询页面失败，applicationId: {}, pageUuid: {}", applicationId, menuUuid);
            return null;
        }
        return respDTO.getId();
    }

    @Override
    public String findTableUuidByAppIdAndPageUuid(Long applicationId, String menuUuid) {
        return appResourceApi.findTableUuidByAppIdAndPageUuid(applicationId, menuUuid);
    }

}
