package com.cmsr.onebase.module.bpm.build.vo.design.strategy.impl;

import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.build.vo.design.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.dto.node.CopyReceiverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.CopyReceiverConfigDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.RoleDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.UserDTO;
import com.cmsr.onebase.module.bpm.core.enums.*;
import com.cmsr.onebase.module.bpm.core.vo.design.node.CopyReceiverNodeVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 抄送人节点配置策略实现
 *
 */
@Slf4j
@Component
public class CopyReceiverNodeVOStrategy extends AbstractNodeVOStrategy<CopyReceiverNodeVO, CopyReceiverNodeExtDTO> {

    @Override
    public CopyReceiverNodeVO createNodeVO() {
        return new CopyReceiverNodeVO();
    }

    @Override
    public void doParseExtData(CopyReceiverNodeVO nodeVO, String extData) {
        // 解析JSON字符串为抄送人扩展数据结构
        CopyReceiverNodeExtDTO nodeExtDTO = JsonUtils.parseObject(extData, CopyReceiverNodeExtDTO.class);

        if (nodeExtDTO != null) {
            // 确保data对象存在
            if (nodeVO.getData() == null) {
                nodeVO.setData(new CopyReceiverNodeVO.CopyReceiverNodeDataVO());
            }

            // 设置抄送人配置
            nodeVO.getData().setCopyReceiverConfig(nodeExtDTO.getCopyReceiverConfig());


            // 设置字段权限配置
            nodeVO.getData().setFieldPermConfig(nodeExtDTO.getFieldPermConfig());

            log.info("成功解析抄送人节点扩展数据: {}", extData);
        }
    }

    @Override
    public CopyReceiverNodeExtDTO buildExtData(CopyReceiverNodeVO nodeVO, Long appId) {
        CopyReceiverNodeExtDTO extDTO = new CopyReceiverNodeExtDTO();

        // 设置节点类型
        extDTO.setNodeType(getSupportedNodeType());

        // 设置抄送人配置
        CopyReceiverNodeVO.CopyReceiverNodeDataVO dataVO = nodeVO.getData();

        // 校验抄送人配置
        validateCopyReceiverConfig(dataVO.getCopyReceiverConfig(), appId);


        extDTO.setCopyReceiverConfig(nodeVO.getData().getCopyReceiverConfig());
        extDTO.setFieldPermConfig(nodeVO.getData().getFieldPermConfig());

        // 设置元数据
        extDTO.setMeta(nodeVO.getMeta());

        return extDTO;
    }

    @Override
    public NodePermFlagDTO buildPermissionFlag(CopyReceiverNodeExtDTO extDTO) {
        NodePermFlagDTO nodePermTagDTO = new NodePermFlagDTO();
        CopyReceiverConfigDTO copyReceiverConfig = extDTO.getCopyReceiverConfig();

        String copyReceiverType = copyReceiverConfig.getCopyReceiverType();
        CopyReceiverTypeEnum copyReceiverTypeEnum = CopyReceiverTypeEnum.getByCode(copyReceiverType);

        if (copyReceiverTypeEnum == CopyReceiverTypeEnum.USER) {
            for (UserDTO user : copyReceiverConfig.getUsers()) {
                if (nodePermTagDTO.getUserIds() == null) {
                    nodePermTagDTO.setUserIds(new ArrayList<>());
                }

                nodePermTagDTO.getUserIds().add(user.getUserId());
            }
        } else if (copyReceiverTypeEnum == CopyReceiverTypeEnum.ROLE) {
            for (RoleDTO role : copyReceiverConfig.getRoles()) {
                if (nodePermTagDTO.getRoleIds() == null) {
                    nodePermTagDTO.setRoleIds(new ArrayList<>());
                }

                nodePermTagDTO.getRoleIds().add(role.getRoleId());
            }
        }

        return nodePermTagDTO;
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.CC.getCode();
    }

    protected void validateCopyReceiverConfig(CopyReceiverConfigDTO copyReceiverConfig, Long appId) {
        String copyReceiverType = copyReceiverConfig.getCopyReceiverType() ;
        CopyReceiverTypeEnum copyReceiverTypeEnum = CopyReceiverTypeEnum.getByCode(copyReceiverType);

        if (copyReceiverTypeEnum == null) {
            log.error("未知的抄送人类型: {}", copyReceiverType);
            throw exception(ErrorCodeConstants.UNSUPPORT_NODE_CC_TYPE);
        }

        if (copyReceiverTypeEnum == CopyReceiverTypeEnum.USER) {
            List<UserDTO> users = copyReceiverConfig.getUsers();

            if (CollUtil.isEmpty(users)) {
                log.error("缺少抄送用户列表 {}", copyReceiverConfig);
                throw exception(ErrorCodeConstants.CC_NODE_USER_LIST_EMPTY);


            }

            // 根据用户ID去重
            Map<Long, UserDTO> userMap = CollectionUtils.convertMap(users, UserDTO::getUserId);

            // 转换为列表
            users = new ArrayList<>(userMap.values());

            // 抄送人列表最多500个用户
            if (users.size() > BpmConstants.MAX_NODE_CC_USERS) {
                log.warn("抄送人列表最多500个用户 当前为：{}", users.size());

                users = new ArrayList<>(users.subList(0, BpmConstants.MAX_NODE_CC_USERS));
            }

            copyReceiverConfig.setUsers(users);

            // 清空角色列表
            copyReceiverConfig.setRoles(null);
        } else if (copyReceiverTypeEnum == CopyReceiverTypeEnum.ROLE) {
            List<RoleDTO> roles = copyReceiverConfig.getRoles();
            if (CollUtil.isEmpty(roles)) {
                log.error("缺少抄送角色列表 {}", copyReceiverConfig);
                throw exception(ErrorCodeConstants.CC_NODE_ROLE_LIST_EMPTY);
            }

            // 此处需要过滤非当前应用的角色，todo：或抛出异常
            List<Long> roleIds = appAuthRoleUser.findRoleIdsByAppId(appId);

            roles.removeIf(role -> !roleIds.contains(role.getRoleId()));

            if (CollUtil.isEmpty(roles)) {
                log.error("缺少有效的角色列表 {}", copyReceiverConfig);
                throw exception(ErrorCodeConstants.MISSING_VALID_CC_ROLE_LIST);
            }

            // 根据用户ID去重
            Map<Long, RoleDTO> roleMap = CollectionUtils.convertMap(roles, RoleDTO::getRoleId);

            // 转换为列表
            roles = new ArrayList<>(roleMap.values());

            // 抄送人列表最多50个角色
            if (roles.size() > BpmConstants.MAX_NODE_CC_ROLES) {
                log.warn("抄送人列表最多50个角色 当前为：{}", roles.size());
                roles = new ArrayList<>(roles.subList(0, BpmConstants.MAX_NODE_CC_ROLES));
            }

            copyReceiverConfig.setRoles(roles);

            // 清空用户列表
            copyReceiverConfig.setUsers(null);
        }

    }



}
