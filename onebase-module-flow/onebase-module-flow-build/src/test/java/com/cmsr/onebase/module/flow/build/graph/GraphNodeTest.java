package com.cmsr.onebase.module.flow.build.graph;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/29 20:26
 */
public class GraphNodeTest {

    @Test
    public void test() throws IOException {
        URL resource = GraphNode.class.getClassLoader().getResource("graph.json");
        String inputJson = IOUtils.toString(resource, "UTF-8");
        //System.out.println(inputJson);
        Graph graph = JsonUtils.parseObject(inputJson, Graph.class);
        List<Long> allFieldId = graph.findAllFieldId();
        System.out.println(allFieldId);
        System.out.println(JsonUtils.toJsonPrettyString(graph));
    }
}