package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppApplicationDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppVersionDO;

import lombok.Data;

/**
 * 应用版本导入数据包
 *
 * @author zhoumingji
 * @date 2026/01/13
 */
@Data
public class ImportPackage {

    /**
     * 应用信息
     */
    private AppApplicationDO applicationDO;

    /**
     * 版本信息
     */
    private AppVersionDO versionDO;

    /**
     * 配置数据
     */
    private ApplicationVersionConfigData configData;
}
