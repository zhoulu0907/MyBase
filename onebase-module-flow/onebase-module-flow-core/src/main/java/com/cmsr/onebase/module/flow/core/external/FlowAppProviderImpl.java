package com.cmsr.onebase.module.flow.core.external;

import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/12/9 9:20
 */
@Component
public class FlowAppProviderImpl implements FlowAppProvider {

    @Autowired
    private AppResourceApi appResourceApi;


    @Override
    public Long findPageIdByAppIdAndPageUuid(Long applicationId, String menuUuid) {
        PageRespDTO respDTO = appResourceApi.findPageByPageUuid(applicationId, menuUuid);
        return respDTO.getId();
    }

    @Override
    public String findTableUuidByAppIdAndPageUuid(Long applicationId, String menuUuid) {
        return appResourceApi.findTableUuidByAppIdAndPageUuid(applicationId, menuUuid);
    }

}
