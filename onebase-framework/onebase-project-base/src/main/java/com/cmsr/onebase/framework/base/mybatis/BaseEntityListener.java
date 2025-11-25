package com.cmsr.onebase.framework.base.mybatis;

import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.orm.data.BaseBizEntity;
import com.cmsr.onebase.framework.orm.data.BaseEntity;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

public class BaseEntityListener implements InsertListener, UpdateListener {

    @Override
    public void onInsert(Object o) {
        if (o instanceof BaseEntity baseEntity) {
            Long userId = SecurityFrameworkUtils.getLoginUserId();
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
            baseEntity.setUpdater(SecurityFrameworkUtils.getLoginUserId());
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
