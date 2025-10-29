package com.cmsr.onebase.module.app.api.app;

import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
@Tag(name = "RPC 服务 - 应用")
public interface AppApplicationApi {

    Long countApplicationByTenantId(Long tenantId);

    List<ApplicationDO> finAppApplicationAll();

}
