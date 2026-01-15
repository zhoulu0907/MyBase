package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.module.system.dal.dataobject.sms.SmsCodeDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemSmsCodeMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.framework.data.base.BaseDO.CREATE_TIME;

/**
 * 短信验证码数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class SmsCodeDataRepository extends BaseDataRepository<SystemSmsCodeMapper, SmsCodeDO> {

    /**
     * 根据手机号查找最后一条短信验证码
     *
     * @param mobile 手机号
     * @return 短信验证码
     */
    public SmsCodeDO findLastByMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return null;
        }
        return getOne(query()
                .eq(SmsCodeDO.MOBILE, mobile)
                .orderBy(CREATE_TIME, false));
    }

    /**
     * 根据手机号、验证码和场景查找短信验证码（取最后一条）
     *
     * @param mobile 手机号
     * @param code 验证码
     * @param scene 场景
     * @return 短信验证码
     */
    public SmsCodeDO findLastByMobileAndCodeAndScene(String mobile, String code, Integer scene) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code) || scene == null) {
            return null;
        }
        return getOne(query()
                .eq(SmsCodeDO.MOBILE, mobile)
                .eq(SmsCodeDO.CODE, code)
                .eq(SmsCodeDO.SCENE, scene)
                .orderBy(CREATE_TIME, false));
    }
}
