package com.cmsr.onebase.module.flow.graph;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/1 11:01
 */
@lombok.Data
public class JsonNode {

    private String id;

    private String type;

    //TODO 待完善，变成具体的类
    private Map<String, Object> data;

    private List<JsonNode> blocks;

    public String toDefine(){
        StringBuilder define = new StringBuilder();
        define.append(type).append(".id(\"").append(id).append("\")");
        return define.toString();
    }
}
