package com.cmsr.onebase.framework.base.mybatis;

import com.cmsr.onebase.framework.data.BaseBizEntity;
import com.cmsr.onebase.framework.data.BaseEntity;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

public class BaseEntityListener implements InsertListener, UpdateListener {

    @Override
    public void onInsert(Object o) {
        if (o instanceof BaseEntity baseEntity) {
            Long userId = WebFrameworkUtils.getLoginUserId();
            baseEntity.setCreator(userId);
            baseEntity.setUpdater(userId);
            LocalDateTime now = LocalDateTime.now();
            baseEntity.setCreateTime(now);
            baseEntity.setUpdateTime(now);
        }
        if (o instanceof BaseBizEntity bizEntity) {
            // TODO: set version properties for biz entity if absent
            if (bizEntity.getVersionId() == null) {
//            bizEntity.setVersionId();
            }
            if (StringUtils.isNotBlank(bizEntity.getVersionStatus())) {
//            bizEntity.setVersionStatus();
            }
        }
    }

    @Override
    public void onUpdate(Object o) {
        if (o instanceof BaseEntity baseEntity) {
            baseEntity.setUpdater(WebFrameworkUtils.getLoginUserId());
            baseEntity.setUpdateTime(LocalDateTime.now());
        }
        if (o instanceof BaseBizEntity bizEntity) {
            // TODO: set version properties for biz entity if absent
            if (bizEntity.getVersionId() == null) {
//            bizEntity.setVersionId();
            }
            if (StringUtils.isNotBlank(bizEntity.getVersionStatus())) {
//            bizEntity.setVersionStatus();
            }
        }
    }
}
