package com.cmsr.onebase.module.bpm.build.vo.design.node.strategy;

import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.core.enums.ApprovalModeEnum;
import com.cmsr.onebase.module.bpm.core.enums.ApproverTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmActionButtonEnum;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeVO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 节点配置创建策略抽象类
 *
 * @author liyang
 * @date 2025-10-23
 */
@Slf4j
public abstract class AbstractNodeVOStrategy<T extends BaseNodeVO, E extends BaseNodeExtDTO> implements NodeVOStrategy<T, E> {
    @Resource
    protected AppAuthRoleUser appAuthRoleUser;

    /**
     * 解析 ext 数据并填充到节点配置VO中
     *
     * @param nodeVO  节点配置VO
     * @param extData ext 数据字符串
     */
    @Override
    public void parseExtData(T nodeVO, String extData) {
        if (nodeVO == null) {
            log.warn("节点配置VO为空，无法解析 ext 数据");
            return;
        }

        if (StringUtils.isBlank(extData)) {
            log.debug("ext 数据为空，跳过解析");
            return;
        }

        // 调用实际子类的解析方法
        doParseExtData(nodeVO, extData);
    }

    /**
     * 实际子类实现解析方法
     *
     * @param nodeVO  节点VO
     * @param extData ext 数据字符串
     */
    public abstract void doParseExtData(T nodeVO, String extData);

    /**
     * 默认的扩展数据构建方法
     * 子类必须重写此方法来实现特定的扩展数据构建逻辑
     *
     * @param nodeVO 节点VO
     * @return 扩展数据对象
     */
    @Override
    public abstract E buildExtData(T nodeVO, Long appId);

    @Override
    public NodePermFlagDTO buildPermissionFlag(E extDTO) {
        return null;
    }

    /**
     * 默认的按钮配置构建方法
     * <p>
     * 主要用与开始节点和发起节点
     */
    protected List<BaseNodeBtnCfgDTO> buildDefaultButtonConfigs() {
        List<BaseNodeBtnCfgDTO> buttonConfigs = new java.util.ArrayList<>();

        // 保存按钮
        BaseNodeBtnCfgDTO btnCfg = new BaseNodeBtnCfgDTO();
        btnCfg.setButtonName(BpmActionButtonEnum.SAVE.getName());
        btnCfg.setEnabled(true);
        btnCfg.setDisplayName(BpmActionButtonEnum.SAVE.getName());
        btnCfg.setButtonType(BpmActionButtonEnum.SAVE.getCode());
        btnCfg.setDefaultApprovalComment("");
        btnCfg.setApprovalCommentRequired(false);

        buttonConfigs.add(btnCfg);

        // 提交按钮
        btnCfg = new BaseNodeBtnCfgDTO();
        btnCfg.setButtonName(BpmActionButtonEnum.SUBMIT.getName());
        btnCfg.setEnabled(true);
        btnCfg.setDisplayName(BpmActionButtonEnum.SUBMIT.getName());
        btnCfg.setButtonType(BpmActionButtonEnum.SUBMIT.getCode());
        btnCfg.setDefaultApprovalComment("");
        btnCfg.setApprovalCommentRequired(false);

        buttonConfigs.add(btnCfg);

        return buttonConfigs;
    }

    protected void validateApproverConfig(ApproverConfigDTO approverConfig, Long appId) {
        String approverType = approverConfig.getApproverType();
        ApproverTypeEnum approverTypeEnum = ApproverTypeEnum.getByCode(approverType);

        if (approverTypeEnum == null) {
            log.error("未知的审批人类型: {}", approverType);
            throw exception(ErrorCodeConstants.UNSUPPORT_NODE_APPROVER_TYPE);
        }

        if (approverTypeEnum == ApproverTypeEnum.USER) {
            List<UserDTO> users = approverConfig.getUsers();

            if (CollectionUtils.isEmpty(users)) {
                log.error("缺少审批用户列表 {}", approverConfig);
                throw exception(ErrorCodeConstants.MISSING_NODE_USER_LIST);
            }

            // 清空角色列表
            approverConfig.setRoles(null);
        } else if (approverTypeEnum == ApproverTypeEnum.ROLE) {
            List<RoleDTO> roles = approverConfig.getRoles();
            if (CollectionUtils.isEmpty(roles)) {
                log.error("缺少审批角色列表 {}", approverConfig);
                throw exception(ErrorCodeConstants.MISSING_NODE_ROLE_LIST);
            }

            // 此处需要过滤非当前应用的角色，todo：或抛出异常
            List<Long> roleIds = appAuthRoleUser.findRoleIdsByAppId(appId);

            roles.removeIf(role -> !roleIds.contains(role.getRoleId()));

            if (CollectionUtils.isEmpty(roles)) {
                log.error("缺少有效的角色列表 {}", approverConfig);
                throw exception(ErrorCodeConstants.MISSING_NODE_VALID_ROLE_LIST);
            }

            // 清空用户列表
            approverConfig.setUsers(null);
        }

        String approvalMode = approverConfig.getApprovalMode();
        ApprovalModeEnum approvalModeEnum = ApprovalModeEnum.getByCode(approvalMode);

        if (approvalModeEnum == null) {
            log.error("未知的审批方式: {}", approvalMode);
            throw exception(ErrorCodeConstants.UNSUPPORT_NODE_APPROVAL_MODE);
        }
    }

    protected void validateBaseButtonConfig(BaseNodeBtnCfgDTO buttonConfig) {
        String buttonType = buttonConfig.getButtonType();

        BpmActionButtonEnum buttonEnum = BpmActionButtonEnum.getByCode(buttonType);
        if (buttonEnum == null) {
            log.error("未知的按钮类型: {}", buttonType);
            throw exception(ErrorCodeConstants.UNSUPPORT_ACTION_BUTTON_TYPE);
        }

        // 按钮名称不可修改
        buttonConfig.setButtonName(buttonEnum.getName());

        if (StringUtils.isBlank(buttonConfig.getDisplayName())) {
            buttonConfig.setDisplayName(buttonEnum.getName());
        }

        if (StringUtils.isBlank(buttonConfig.getDefaultApprovalComment())) {
            buttonConfig.setDefaultApprovalComment(buttonEnum.getName());
        }
    }

    protected void validateBaseButtonConfigs(List<BaseNodeBtnCfgDTO> buttonConfigs) {
        if (CollectionUtils.isEmpty(buttonConfigs)) {
            log.error("缺少审批按钮配置");
            throw exception(ErrorCodeConstants.MISSING_NODE_BTN_CFG);
        }

        for (BaseNodeBtnCfgDTO buttonConfig : buttonConfigs) {
            validateBaseButtonConfig(buttonConfig);
        }
    }

    protected void validateButtonConfigs(List<ApproverNodeBtnCfgDTO> buttonConfigs) {
        // todo 去除重复的按钮
        if (CollectionUtils.isEmpty(buttonConfigs)) {
            log.error("缺少审批按钮配置");
            throw exception(ErrorCodeConstants.MISSING_NODE_BTN_CFG);
        }

        int enabledCount = 0;
        for (ApproverNodeBtnCfgDTO buttonConfig : buttonConfigs) {
            validateBaseButtonConfig(buttonConfig);

            if (buttonConfig.getEnabled()) {
                enabledCount++;
            }
        }

        if (enabledCount == 0) {
            log.error("审批按钮配置中必须开启至少一个按钮");
            throw exception(ErrorCodeConstants.APPROVER_NODE_REQUIRED_ENABLED_BTN);
        }
    }
}
