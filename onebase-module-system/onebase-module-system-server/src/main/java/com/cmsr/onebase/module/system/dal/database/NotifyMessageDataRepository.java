package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.notify.NotifyMessageDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 站内信数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class NotifyMessageDataRepository extends DataRepository<NotifyMessageDO> {

    public NotifyMessageDataRepository() {
        super(NotifyMessageDO.class);
    }

    /**
     * 分页查询站内信
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<NotifyMessageDO> findPage(NotifyMessagePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (null != pageReqVO.getUserId()) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.USER_ID, pageReqVO.getUserId());
        }
        if (null != pageReqVO.getUserType()) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.USER_TYPE, pageReqVO.getUserType());
        }
        if (StringUtils.isNotBlank(pageReqVO.getTemplateCode())) {
            configStore.and(Compare.LIKE, NotifyMessageDO.TEMPLATE_CODE, pageReqVO.getTemplateCode());
        }
        if (null != pageReqVO.getTemplateType()) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.TEMPLATE_TYPE, pageReqVO.getTemplateType());
        }
        if (null != pageReqVO.getCreateTime()) {
            configStore.and(Compare.EQUAL, "create_time", pageReqVO.getCreateTime());
        }

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    /**
     * 分页查询我的站内信
     *
     * @param pageReqVO 分页查询参数
     * @param userId    用户ID
     * @param userType  用户类型
     * @return 分页结果
     */
    public PageResult<NotifyMessageDO> findMyPage(NotifyMessageMyPageReqVO pageReqVO, Long userId, Integer userType) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (null != userId) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.USER_ID, userId);
        }
        if (null != userType) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.USER_TYPE, userType);
        }

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    /**
     * 查询未读站内信列表
     *
     * @param userId   用户ID
     * @param userType 用户类型
     * @param size     数量限制
     * @return 未读站内信列表
     */
    public List<NotifyMessageDO> findUnreadList(Long userId, Integer userType, Integer size) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (null != userId) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.USER_ID, userId);
        }
        if (null != userType) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.USER_TYPE, userType);
        }
        configStore.and(Compare.EQUAL, NotifyMessageDO.READ_STATUS, false);
        if (null != size) {
            configStore.limit(size);
        }

        return findAllByConfig(configStore);
    }

    /**
     * 统计未读站内信数量
     *
     * @param userId   用户ID
     * @param userType 用户类型
     * @return 未读站内信数量
     */
    public Long countUnread(Long userId, Integer userType) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (null != userId) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.USER_ID, userId);
        }
        if (null != userType) {
            configStore.and(Compare.EQUAL, NotifyMessageDO.USER_TYPE, userType);
        }
        configStore.and(Compare.EQUAL, NotifyMessageDO.READ_STATUS, false);

        List<NotifyMessageDO> notifyMessageDOList = findAllByConfig(configStore);
        return (long) notifyMessageDOList.size();
    }

    /**
     * 批量更新站内信为已读
     *
     * @param ids      站内信ID集合
     * @param userId   用户ID
     * @param userType 用户类型
     * @return 更新数量
     */
    public int updateReadStatus(Collection<Long> ids, Long userId, Integer userType) {
        ConfigStore configStore = new DefaultConfigStore()
                .and(Compare.IN, "id", ids)
                .and(Compare.EQUAL, NotifyMessageDO.USER_ID, userId)
                .and(Compare.EQUAL, NotifyMessageDO.USER_TYPE, userType);
        DataRow row = new DataRow();
        row.put(NotifyMessageDO.READ_STATUS, true);
        return (int) updateByConfig(row, configStore);
    }

    /**
     * 更新用户所有站内信为已读
     *
     * @param userId   用户ID
     * @param userType 用户类型
     * @return 更新数量
     */
    public int updateAllReadStatus(Long userId, Integer userType) {
        ConfigStore configStore = new DefaultConfigStore()
                .and(Compare.EQUAL, NotifyMessageDO.USER_ID, userId)
                .and(Compare.EQUAL, NotifyMessageDO.USER_TYPE, userType);

        DataRow row = new DataRow();
        row.put(NotifyMessageDO.READ_STATUS, true);
        return (int) updateByConfig(row, configStore);
    }
}
