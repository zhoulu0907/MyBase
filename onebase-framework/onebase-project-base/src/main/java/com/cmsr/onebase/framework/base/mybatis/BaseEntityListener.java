package com.cmsr.onebase.framework.base.mybatis;

import com.cmsr.onebase.framework.data.BaseEntity;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;

import java.time.LocalDateTime;

public class BaseEntityListener implements InsertListener, UpdateListener {

    @Override
    public void onInsert(Object o) {
        if (o instanceof BaseEntity baseDO) {
            Long userId = WebFrameworkUtils.getLoginUserId();
            baseDO.setCreator(userId);
            baseDO.setUpdater(userId);
            LocalDateTime now = LocalDateTime.now();
            baseDO.setCreateTime(now);
            baseDO.setUpdateTime(now);
        }
    }

    @Override
    public void onUpdate(Object o) {
        if (o instanceof BaseEntity baseDO) {
            baseDO.setUpdater(WebFrameworkUtils.getLoginUserId());
            baseDO.setUpdateTime(LocalDateTime.now());
        }
    }
}
