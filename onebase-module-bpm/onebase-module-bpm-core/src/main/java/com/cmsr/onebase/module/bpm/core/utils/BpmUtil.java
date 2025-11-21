package com.cmsr.onebase.module.bpm.core.utils;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.NodeJson;

import java.util.Objects;

/**
 * 流程工具类
 *
 * @author liyang
 * @date 2025-11-12
 */
public class BpmUtil {
    /**
     * 根据节点编码获取节点扩展DTO
     *
     * @param nodeCode 节点编码
     * @param defJsonStr 流程定义JSON字符串
     * @return 节点扩展DTO，如果不存在则返回null
     */
    public static BaseNodeExtDTO getNodeExtDTOByNodeCode(String nodeCode, String defJsonStr) {
        if (StringUtils.isBlank(defJsonStr)) {
            return null;
        }

        DefJson defJson = JsonUtils.parseObject(defJsonStr, DefJson.class);
        return getNodeExtDTOByNodeCode(nodeCode, defJson);
    }

    /**
     * 根据节点编码获取节点扩展DTO
     *
     * @param nodeCode 节点编码
     * @param defJson 流程定义JSON对象
     * @return 节点扩展DTO，如果不存在则返回null
     */
    public static BaseNodeExtDTO getNodeExtDTOByNodeCode(String nodeCode, DefJson defJson) {
        if (defJson == null || defJson.getNodeList() == null) {
            return null;
        }

        NodeJson currNodeJson = null;

        for (NodeJson nodeJson : defJson.getNodeList()) {
            if (Objects.equals(nodeJson.getNodeCode(), nodeCode)) {
                currNodeJson = nodeJson;
                break;
            }
        }

        if (currNodeJson == null) {
            return null;
        }

        return JsonUtils.parseObject(currNodeJson.getExt(), BaseNodeExtDTO.class);
    }

    /**
     * 获取流程定义中的发起节点
     *
     * @param defJsonStr 流程定义JSON字符串
     * @return 发起节点JSON对象，如果不存在则返回null
     */

    public static NodeJson getInitiationNodeJson(String defJsonStr) {

        if (StringUtils.isBlank(defJsonStr)) {
            return null;
        }

        DefJson defJson = JsonUtils.parseObject(defJsonStr, DefJson.class);
        if (defJson == null || defJson.getNodeList() == null) {
            return null;
        }

        NodeJson node = null;

        for (NodeJson nodeJson : defJson.getNodeList()) {
            BaseNodeExtDTO extDTO =  JsonUtils.parseObject(nodeJson.getExt(), BaseNodeExtDTO.class);
            if (Objects.equals(extDTO.getNodeType(), BpmNodeTypeEnum.INITIATION.getCode())) {
                node = nodeJson;
                break;
            }
        }

        if (node == null) {
            return null;
        }

        return node;
    }
}
