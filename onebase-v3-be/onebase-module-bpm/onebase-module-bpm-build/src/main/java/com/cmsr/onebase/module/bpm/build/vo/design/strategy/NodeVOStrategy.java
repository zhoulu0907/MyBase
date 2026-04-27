package com.cmsr.onebase.module.bpm.build.vo.design.strategy;

import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeVO;

/**
 * 节点配置创建策略接口
 *
 * @author liyang
 * @date 2025-10-23
 */
public interface NodeVOStrategy<T extends BaseNodeVO, E extends BaseNodeExtDTO> {

    /**
     * 创建节点配置VO
     *
     * @return BaseNodeVO 对应的子类实例
     */
    BaseNodeVO createNodeVO();

    /**
     * 解析 ext 数据并填充到节点VO中
     *
     * @param nodeVO 节点VO
     * @param extData ext 数据字符串
     */
    void parseExtData(T nodeVO, String extData);

    /**
     * 将节点配置VO转换为扩展数据
     * 用于反向转换：从BpmDefJsonVO.NodeVO转换为NodeJson.ext
     *
     * @param nodeVO 节点配置VO
     * @return 扩展数据对象
     */
    E buildExtData(T nodeVO, Long appId);

    /**
     * 将扩展数据对象转换为权限标签字符串
     *
     * @param extDTO 扩展数据对象
     * @return 权限标签字符串
     */
    NodePermFlagDTO buildPermissionFlag(E extDTO);

    /**
     * 获取支持的节点类型
     *
     * @return 节点类型编码
     */
    String getSupportedNodeType();
}
