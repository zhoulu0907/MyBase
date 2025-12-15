package com.cmsr.onebase.module.flow.component.interact;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:07
 */
@Data
@NodeType("navigate")
public class NavigateNodeData extends NodeData implements Serializable {

    private String targetPageType;

    private String pageId;

    private String outsideUrl;

    private String openPageType;

    private String modalSizeType;

    private String modalWidth;

    private String modalHeight;

    private String modalTitle;

    private String modalPlacement;

    private String authorize;

    private String unAuthorizedEvent;

    private List<Field> paramFields;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("targetPageType", targetPageType);
        map.put("pageId", pageId);
        map.put("outsideUrl", outsideUrl);
        map.put("openPageType", openPageType);
        map.put("modalSizeType", modalSizeType);
        map.put("modalWidth", modalWidth);
        map.put("modalHeight", modalHeight);
        map.put("modalTitle", modalTitle);
        map.put("modalPlacement", modalPlacement);
        map.put("authorize", authorize);
        map.put("unAuthorizedEvent", unAuthorizedEvent);
        map.put("paramFields", paramFields);
        return map;
    }

    @Data
    public static class Field implements Serializable {

        private String id;

        private String fieldName;

        private String value;

    }

}
