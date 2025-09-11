package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsCodeDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

/**
 * 短信验证码数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class SmsCodeDataRepository extends DataRepository<SmsCodeDO> {

    public SmsCodeDataRepository() {
        super(SmsCodeDO.class);
    }

    /**
     * 根据手机号查找最后一条短信验证码
     *
     * @param mobile 手机号
     * @return 短信验证码
     */
    public SmsCodeDO findLastByMobile(String mobile) {
        return findOne(new DefaultConfigStore()
                .and(Compare.EQUAL, SmsCodeDO.MOBILE, mobile)
                .order("create_time", "desc"));
    }

    /**
     * 根据手机号、验证码和场景查找短信验证码
     *
     * @param mobile 手机号
     * @param code 验证码
     * @param scene 场景
     * @return 短信验证码
     */
    public SmsCodeDO findLastByMobileAndCodeAndScene(String mobile, String code, Integer scene) {
        return findOne(new DefaultConfigStore()
                .and(Compare.EQUAL, SmsCodeDO.MOBILE, mobile)
                .and(Compare.EQUAL, SmsCodeDO.CODE, code)
                .and(Compare.EQUAL, SmsCodeDO.SCENE, scene)
                .order("create_time", "desc"));
    }
}
