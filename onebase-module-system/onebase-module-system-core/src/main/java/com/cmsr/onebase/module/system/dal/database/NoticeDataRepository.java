package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.notice.vo.NoticePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.notice.NoticeDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import org.apache.commons.lang3.StringUtils;

/**
 * 通知公告数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class NoticeDataRepository extends DataRepository<NoticeDO> {

    public NoticeDataRepository() {
        super(NoticeDO.class);
    }

    /**
     * 分页查询通知公告
     *
     * @param reqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<NoticeDO> findPage(NoticePageReqVO reqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (StringUtils.isNotBlank(reqVO.getTitle())) {
            configStore.and(Compare.LIKE, NoticeDO.TITLE, reqVO.getTitle());
        }
        if (null != reqVO.getStatus()) {
            configStore.and(Compare.EQUAL, NoticeDO.STATUS, reqVO.getStatus());
        }

        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }
}
