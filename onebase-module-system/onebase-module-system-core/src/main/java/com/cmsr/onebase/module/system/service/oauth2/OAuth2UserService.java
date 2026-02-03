package com.cmsr.onebase.module.system.service.oauth2;

import com.cmsr.onebase.module.system.vo.user.UserSimpleRespVO;

/**
 * OAuth2.0 user Service 接口
 *
 *
 */
public interface OAuth2UserService {

    /**
     * 获得用户信息
     *
     * @param accessToken 访问令牌
     * @return 用户信息
     */
    UserSimpleRespVO getUserInfoByToken(String accessToken);

}
