package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.socail.vo.user.SocialUserPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.social.SocialUserDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

/**
 * 社交用户数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class SocialUserDataRepository extends DataRepository<SocialUserDO> {

    public SocialUserDataRepository() {
        super(SocialUserDO.class);
    }

    /**
     * 根据类型和openid查找社交用户
     *
     * @param type 社交类型
     * @param openid openid
     * @return 社交用户
     */
    public SocialUserDO findByTypeAndOpenid(Integer type, String openid) {
        return findOne(new DefaultConfigStore()
                .and(Compare.EQUAL, SocialUserDO.TYPE, type)
                .and(Compare.EQUAL, SocialUserDO.OPENID, openid));
    }

    /**
     * 根据类型、code和state查找社交用户
     *
     * @param type 社交类型
     * @param code 授权码
     * @param state 状态
     * @return 社交用户
     */
    public SocialUserDO findByTypeAndCodeAndState(Integer type, String code, String state) {
        return findOne(new DefaultConfigStore()
                .and(Compare.EQUAL, SocialUserDO.TYPE, type)
                .and(Compare.EQUAL, SocialUserDO.CODE, code)
                .and(Compare.EQUAL, SocialUserDO.STATE, state));
    }

    /**
     * 分页查询社交用户
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<SocialUserDO> findPage(SocialUserPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (pageReqVO.getType() != null) {
            configStore.and(Compare.EQUAL, SocialUserDO.TYPE, pageReqVO.getType());
        }
        if (pageReqVO.getNickname() != null) {
            configStore.and(Compare.LIKE, SocialUserDO.NICKNAME, pageReqVO.getNickname());
        }
        if (pageReqVO.getOpenid() != null) {
            configStore.and(Compare.EQUAL, SocialUserDO.OPENID, pageReqVO.getOpenid());
        }
        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            configStore.and(Compare.GREAT_EQUAL, "create_time", pageReqVO.getCreateTime()[0])
                       .and(Compare.LESS_EQUAL, "create_time", pageReqVO.getCreateTime()[1]);
        }

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
