package com.cmsr.onebase.module.app.core.dal.provider.auth;

import com.cmsr.onebase.module.app.api.security.bo.FieldPermissionItem;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/28 20:15
 */
@Slf4j
@Setter
@Service
public class AppAuthFieldProvider {

    public List<FieldPermissionItem> findFields(Long applicationId, Set<Long> roleIds, Long menuId) {
        return null;
    }
}
