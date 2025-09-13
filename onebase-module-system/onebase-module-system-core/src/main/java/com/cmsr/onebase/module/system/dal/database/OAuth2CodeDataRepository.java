package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2CodeDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

/**
 * OAuth2授权码数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class OAuth2CodeDataRepository extends DataRepository<OAuth2CodeDO> {

    public OAuth2CodeDataRepository() {
        super(OAuth2CodeDO.class);
    }

    /**
     * 根据授权码查找OAuth2授权码
     *
     * @param code 授权码
     * @return OAuth2授权码
     */
    public OAuth2CodeDO findOneByCode(String code) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, OAuth2CodeDO.CODE, code));
    }
}
