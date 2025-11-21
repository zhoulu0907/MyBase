package com.cmsr.onebase.module.bpm.core.utils;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
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
     * 根据节点类型获取节点JSON对象
     *
     * @param nodeType 节点类型
     * @param defJsonStr 节点JSON字符串
     * @return 节点JSON对象，如果未找到则返回null
     */

    public static NodeJson getNodeJsonByNodeType(String nodeType, String defJsonStr) {

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
            if (Objects.equals(extDTO.getNodeType(), nodeType)) {
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
