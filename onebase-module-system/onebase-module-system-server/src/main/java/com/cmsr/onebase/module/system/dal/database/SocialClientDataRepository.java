package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.socail.vo.client.SocialClientPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.social.SocialClientDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

/**
 * 社交客户端数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class SocialClientDataRepository extends DataRepositoryNew<SocialClientDO> {

    public SocialClientDataRepository() {
        super(SocialClientDO.class);
    }

    /**
     * 根据社交类型和用户类型查找社交客户端
     *
     * @param socialType 社交类型
     * @param userType 用户类型
     * @return 社交客户端
     */
    public SocialClientDO findBySocialTypeAndUserType(Integer socialType, Integer userType) {
        return findOne(new DefaultConfigStore()
                .and(Compare.EQUAL, "social_type", socialType)
                .and(Compare.EQUAL, "user_type", userType));
    }

    /**
     * 分页查询社交客户端
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<SocialClientDO> findPage(SocialClientPageReqVO pageReqVO) {
        return findPageWithConditions(new DefaultConfigStore()
                .and(Compare.EQUAL, "social_type", pageReqVO.getSocialType())
                .and(Compare.EQUAL, "user_type", pageReqVO.getUserType())
                .order("id", Order.TYPE.DESC), pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
