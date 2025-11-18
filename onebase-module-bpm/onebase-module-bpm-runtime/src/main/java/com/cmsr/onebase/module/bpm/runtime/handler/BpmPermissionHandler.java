package com.cmsr.onebase.module.bpm.runtime.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.handler.PermissionHandler;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 办理人权限处理器
 *
 * @author AprilWind
 */
@Component
@Slf4j
public class BpmPermissionHandler implements PermissionHandler {
    @Resource
    private AppAuthRoleUser appAuthRoleUser;

    /**
     * 办理人权限标识，比如用户，角色，部门等，用于校验是否有权限办理任务
     * 后续在{@link FlowParams#getPermissionFlag}  中获取
     * 返回当前用户权限集合
     */
    @Override
    public List<String> permissions() {
        return Collections.singletonList(String.valueOf(WebFrameworkUtils.getLoginUserId()));
    }

    /**
     * 获取当前办理人
     *
     * @return 当前办理人
     */
    @Override
    public String getHandler() {
        return String.valueOf(WebFrameworkUtils.getLoginUserId());
    }

    /**
     * 转换办理人，比如设计器中预设了能办理的人，如果其中包含角色或者部门id等，可以通过此接口进行转换成用户id
     */
    @Override
    public List<String> convertPermissions(List<String> permissions) {
        if (CollectionUtil.isEmpty(permissions)) {
            return permissions;
        }

        Set<String> convertedPermissions = new HashSet<>();

        // todo：是否要兼容原有warmflow格式
        // permissions使用了定义的格式
        for (String permission : permissions) {
            try {
                NodePermFlagDTO nodePermFlagDTO = JsonUtils.parseObject(permission, NodePermFlagDTO.class);

                // 处理用户ID列表
                if (CollectionUtil.isNotEmpty(nodePermFlagDTO.getUserIds())) {
                    for (Long userId : nodePermFlagDTO.getUserIds()) {
                        convertedPermissions.add(String.valueOf(userId));
                    }
                }

                if (CollectionUtil.isNotEmpty(nodePermFlagDTO.getRoleIds())) {
                    List<Long> usersFromRoleId = appAuthRoleUser.findUserIdsByRoleIds(nodePermFlagDTO.getRoleIds());

                    if (CollectionUtil.isNotEmpty(usersFromRoleId)) {
                        for (Long userId : usersFromRoleId) {
                            convertedPermissions.add(String.valueOf(userId));
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("解析权限字符串为NodePermFlagDTO失败，permission={}", permission, e);
            }
        }

        return new ArrayList<>(convertedPermissions);
    }
}

