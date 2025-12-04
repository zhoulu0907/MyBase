package com.cmsr.onebase.framework.orm.config;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.orm.entity.WarmFlowBaseEntity;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;

import java.time.LocalDateTime;

public class DefaultEntityListener implements InsertListener, UpdateListener {

    @Override
    public void onInsert(Object o) {
        if (o instanceof BaseEntity baseEntity) {
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            baseEntity.setCreator(userId);
            baseEntity.setUpdater(userId);
            LocalDateTime now = LocalDateTime.now();
            baseEntity.setCreateTime(now);
            baseEntity.setUpdateTime(now);
        } else if (o instanceof WarmFlowBaseEntity wfBaseEntity) {
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            wfBaseEntity.setCreator(userId);
            wfBaseEntity.setUpdater(userId);
            LocalDateTime now = LocalDateTime.now();
            wfBaseEntity.setCreateTimeByListener(now);
            wfBaseEntity.setUpdateTimeByListener(now);
        }

        if (o instanceof BaseAppEntity appEntity) {
            Long applicationId = ApplicationManager.getApplicationId();
            if (appEntity.getApplicationId() == null && applicationId != null) {
                appEntity.setApplicationId(applicationId);
            }
        }

        if (o instanceof BaseBizEntity bizEntity) {
            Long versionTag = ApplicationManager.getVersionTag();
            if (bizEntity.getVersionTag() == null && versionTag != null) {
                bizEntity.setVersionTag(versionTag);
            }
            Long applicationId = ApplicationManager.getApplicationId();
            if (bizEntity.getApplicationId() == null && applicationId != null) {
                bizEntity.setApplicationId(applicationId);
            }
        }
    }

    @Override
    public void onUpdate(Object o) {
        if (o instanceof BaseEntity baseEntity) {
            baseEntity.setUpdater(SecurityFrameworkUtils.getLoginUserId());
            baseEntity.setUpdateTime(LocalDateTime.now());
        }
    }
}
