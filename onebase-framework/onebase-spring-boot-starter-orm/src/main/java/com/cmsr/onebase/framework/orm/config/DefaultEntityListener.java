package com.cmsr.onebase.framework.orm.config;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.orm.entity.*;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
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

        if (o instanceof WarmFlowBizEntity wfBizEntity) {
            Long versionTag = ApplicationManager.getVersionTag();
            if (wfBizEntity.getVersionTag() == null && versionTag != null) {
                wfBizEntity.setVersionTagByListener(versionTag);
            }
            Long applicationId = ApplicationManager.getApplicationId();
            if (wfBizEntity.getApplicationId() == null && applicationId != null) {
                wfBizEntity.setApplicationIdByListener(applicationId);
            }
        }
    }

    @Override
    public void onUpdate(Object o) {
        if (o instanceof BaseEntity baseEntity) {
            baseEntity.setUpdater(SecurityFrameworkUtils.getLoginUserId());
            baseEntity.setUpdateTime(LocalDateTime.now());
        }

        if (o instanceof WarmFlowBaseEntity wfBaseEntity) {
            wfBaseEntity.setUpdater(SecurityFrameworkUtils.getLoginUserId());
            wfBaseEntity.setUpdateTimeByListener(LocalDateTime.now());
        }
    }
}
