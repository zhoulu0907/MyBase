package com.cmsr.onebase.module.app.core.dal.provider.auth;

import com.cmsr.onebase.module.app.api.security.bo.DataPermissionGroup;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/28 20:06
 */
@Slf4j
@Setter
@Service
public class AppAuthDataGroupProvider {
    public List<DataPermissionGroup> findDataGroups(Long applicationId, Set<Long> roleIds, Long menuId) {
        // 类似 permission 处理，用 role表 left join data_group表，没有权限的 role_id 默认给一个默认值
        return null;
    }
}
