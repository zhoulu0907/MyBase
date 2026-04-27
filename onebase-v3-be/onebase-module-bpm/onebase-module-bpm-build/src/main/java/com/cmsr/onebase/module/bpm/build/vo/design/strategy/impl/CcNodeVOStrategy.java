package com.cmsr.onebase.module.bpm.build.vo.design.strategy.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.build.vo.design.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.dto.node.CcNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.vo.design.node.CcNodeVO;
import lombok.extern.slf4j.Slf4j;
import com.cmsr.onebase.module.bpm.core.enums.BpmConstants;
import org.springframework.stereotype.Component;

/**
 * 抄送人节点配置策略实现
 *
 */
@Slf4j
@Component
public class CcNodeVOStrategy extends AbstractNodeVOStrategy<CcNodeVO, CcNodeExtDTO> {

    @Override
    public CcNodeVO createNodeVO() {
        return new CcNodeVO();
    }

    @Override
    public void doParseExtData(CcNodeVO nodeVO, String extData) {
        // 解析JSON字符串为抄送人扩展数据结构
        CcNodeExtDTO nodeExtDTO = JsonUtils.parseObject(extData, CcNodeExtDTO.class);

        if (nodeExtDTO != null) {
            // 确保data对象存在
            if (nodeVO.getData() == null) {
                nodeVO.setData(new CcNodeVO.CcNodeDataVO());
            }

            // 设置抄送人配置
            nodeVO.getData().setCopyReceiverConfig(nodeExtDTO.getCopyReceiverConfig());


            // 设置字段权限配置
            nodeVO.getData().setFieldPermConfig(nodeExtDTO.getFieldPermConfig());

            log.info("成功解析抄送人节点扩展数据: {}", extData);
        }
    }

    @Override
    public CcNodeExtDTO buildExtData(CcNodeVO nodeVO, Long appId) {
        CcNodeExtDTO extDTO = new CcNodeExtDTO();

        // 设置节点类型
        extDTO.setNodeType(getSupportedNodeType());

        // 设置抄送人配置
        CcNodeVO.CcNodeDataVO dataVO = nodeVO.getData();

        // 校验抄送人配置
        validateHandlerConfig(dataVO.getCopyReceiverConfig(), appId, BpmConstants.MAX_NODE_CC_USERS, BpmConstants.MAX_NODE_CC_ROLES);

        extDTO.setCopyReceiverConfig(nodeVO.getData().getCopyReceiverConfig());
        extDTO.setFieldPermConfig(nodeVO.getData().getFieldPermConfig());

        // 设置元数据
        extDTO.setMeta(nodeVO.getMeta());

        return extDTO;
    }

    @Override
    public NodePermFlagDTO buildPermissionFlag(CcNodeExtDTO extDTO) {
        return buildPermissionFlag(extDTO.getCopyReceiverConfig());
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.CC.getCode();
    }

}
