package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2CodeDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemOauth2CodeMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2CodeDO.CODE;

/**
 * OAuth2 授权码数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class OAuth2CodeDataRepository extends BaseDataRepository<SystemOauth2CodeMapper, OAuth2CodeDO> {

    /**
     * 根据授权码查找 OAuth2 授权码
     *
     * @param code 授权码
     * @return OAuth2 授权码
     */
    public OAuth2CodeDO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getOne(query().eq(CODE, code));
    }
}
