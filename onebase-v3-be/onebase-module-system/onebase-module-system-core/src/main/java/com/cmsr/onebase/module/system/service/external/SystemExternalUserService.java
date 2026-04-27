package com.cmsr.onebase.module.system.service.external;

import com.cmsr.onebase.module.system.dal.dataobject.external.SystemExternalUserDO;

import java.util.List;

/**
 * 外部用户关联服务接口
 *
 * @author matianyu
 * @date 2026-03-17
 */
public interface SystemExternalUserService {

    /**
     * 根据 onebase 用户 id 获取关联
     *
     * @param obUserId onebase 用户 id
     * @return 关联列表
     */
    List<SystemExternalUserDO> getByObUserId(String obUserId);

    /**
     * 根据外部用户 id 获取关联
     *
     * @param externalUserId 外部用户 id
     * @return 关联列表
     */
    SystemExternalUserDO getByExternalUserId(String externalUserId, String platformType);

}

