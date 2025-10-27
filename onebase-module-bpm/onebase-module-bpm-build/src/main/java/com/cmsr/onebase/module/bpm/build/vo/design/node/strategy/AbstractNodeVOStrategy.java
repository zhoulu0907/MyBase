package com.cmsr.onebase.module.bpm.build.vo.design.node.strategy;

import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.base.BaseNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 节点配置创建策略抽象类
 *
 * @author liyang
 * @date 2025-10-23
 */
@Slf4j
public abstract class AbstractNodeVOStrategy<T extends BaseNodeVO, E extends BaseNodeExtDTO> implements NodeVOStrategy<T, E> {

    /**
     * 解析 ext 数据并填充到节点配置VO中
     *
     * @param nodeVO 节点配置VO
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
     * @param nodeVO 节点VO
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
    public abstract E buildExtData(T nodeVO);

    /**
     * 默认的按钮配置构建方法
     *
     * 主要用与开始节点和发起节点
     */
    protected List<BaseNodeBtnCfgDTO> buildDefaultButtonConfigs() {
        List<BaseNodeBtnCfgDTO> buttonConfigs = new java.util.ArrayList<>();

        // 保存按钮
        BaseNodeBtnCfgDTO btnCfg = new BaseNodeBtnCfgDTO();
        btnCfg.setButtonName("保存");
        btnCfg.setEnabled(true);
        btnCfg.setDisplayName("保存");
        btnCfg.setButtonType("save");
        btnCfg.setDefaultApprovalComment("");

        buttonConfigs.add(btnCfg);

        // 提交按钮
        btnCfg = new BaseNodeBtnCfgDTO();
        btnCfg.setButtonName("提交");
        btnCfg.setEnabled(true);
        btnCfg.setDisplayName("提交");
        btnCfg.setButtonType("submit");
        btnCfg.setDefaultApprovalComment("");

        buttonConfigs.add(btnCfg);

        return buttonConfigs;
    }
}
