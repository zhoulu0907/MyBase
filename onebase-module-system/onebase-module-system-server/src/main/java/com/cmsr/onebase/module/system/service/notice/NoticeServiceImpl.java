package com.cmsr.onebase.module.system.service.notice;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.notice.vo.NoticePageReqVO;
import com.cmsr.onebase.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.mail.MailLogDO;
import com.cmsr.onebase.module.system.dal.dataobject.notice.NoticeDO;
import com.cmsr.onebase.module.system.dal.mysql.notice.NoticeMapper;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.NOTICE_NOT_FOUND;

/**
 * 通知公告 Service 实现类
 *
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    //@Resource
    //private NoticeMapper noticeMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public Long createNotice(NoticeSaveReqVO createReqVO) {
        NoticeDO notice = BeanUtils.toBean(createReqVO, NoticeDO.class);
        dataRepository.insert(notice);
        return notice.getId();
    }

    @Override
    public void updateNotice(NoticeSaveReqVO updateReqVO) {
        // 校验是否存在
        validateNoticeExists(updateReqVO.getId());
        // 更新通知公告
        NoticeDO updateObj = BeanUtils.toBean(updateReqVO, NoticeDO.class);
        dataRepository.save(updateObj);
    }

    @Override
    public void deleteNotice(Long id) {
        // 校验是否存在
        validateNoticeExists(id);
        // 删除通知公告
        dataRepository.deleteById(NoticeDO.class,id);
    }

    @Override
    public PageResult<NoticeDO> getNoticePage(NoticePageReqVO reqVO) {

        ConfigStore configStore = new DefaultConfigStore();

        if (StringUtils.isNotBlank(reqVO.getTitle())) {
            configStore.and(Compare.LIKE, "title", reqVO.getTitle());
        }
        if (null != reqVO.getStatus()) {
            configStore.and(Compare.EAUAL, "status", reqVO.getStatus());
        }

        return dataRepository.findPageWithConditions(NoticeDO.class,configStore, reqVO.getPageNo(), reqVO.getPageSize());

    }

    @Override
    public NoticeDO getNotice(Long id) {
        return dataRepository.findById(NoticeDO.class,id);
    }

    @VisibleForTesting
    public void validateNoticeExists(Long id) {
        if (id == null) {
            return;
        }
        NoticeDO notice = dataRepository.findById(NoticeDO.class,id);
        if (notice == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
    }

}
