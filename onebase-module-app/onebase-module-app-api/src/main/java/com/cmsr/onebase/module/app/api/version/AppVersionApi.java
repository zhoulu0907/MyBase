package com.cmsr.onebase.module.app.api.version;

import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.app.dto.TagVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
public interface AppVersionApi {

    Integer getAppThirdEnableStatus(Long appId);


}
