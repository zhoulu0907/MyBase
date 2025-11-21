package com.cmsr.onebase.module.bpm.runtime.service.common.permission;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 流程审批人解析器
 *
 * @author liyang
 * @date 2025-11-19
 */
@Slf4j
@Service
public class BpmPermissionResolver {
    @Resource
    private AppAuthRoleUser appAuthRoleUser;

    public Set<Long> resolveUserIds(String permFlag) {
       return resolveUserIds(permFlag, 0);
    }

    public Set<Long> resolveUserIds(String permFlag, Integer maxUsers) {
        NodePermFlagDTO permFlagDTO = JsonUtils.parseObject(permFlag, NodePermFlagDTO.class);

        if (permFlagDTO == null) {
            log.warn("解析权限标识失败 permFlag: {}", permFlag);
            return new HashSet<>();
        }

        return resolveUserIds(permFlagDTO, maxUsers);
    }

    public Set<Long> resolveUserIds(NodePermFlagDTO permFlagDTO) {
        return resolveUserIds(permFlagDTO, 0);
    }

    public Set<Long> resolveUserIds(NodePermFlagDTO permFlagDTO, Integer maxUsers) {
        Set<Long> userIdSet = new HashSet<>();

        if (CollectionUtils.isNotEmpty(permFlagDTO.getUserIds())) {
            // 处理用户列表
            userIdSet.addAll(permFlagDTO.getUserIds());
        } else if (CollectionUtils.isNotEmpty(permFlagDTO.getRoleIds())) {
            // 处理角色列表
            List<Long> userIds = appAuthRoleUser.findUserIdsByRoleIds(permFlagDTO.getRoleIds());

            if (CollectionUtils.isNotEmpty(userIds)) {
                userIdSet.addAll(userIds);
            }
        } else {
            // todo: 支持更多类型的权限
        }

        // maxUsers = 0 则不做限制
        if (!userIdSet.isEmpty() && maxUsers > 0) {
            // 限制用户数量
            userIdSet = new HashSet<>(userIdSet.stream().limit(maxUsers).toList());
        }

        return userIdSet;
    }
}
