package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.graph.FlowChainBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowGraphBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author：huangjie
 * @Date：2025/9/1 12:11
 */
class JsonGraphTest {


    public void testToFlowChain(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("graphjson/" + fileName);
        String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        FlowGraphBuilder flowGraphBuilder = new FlowGraphBuilder();
        JsonGraph jsonGraph = flowGraphBuilder.build(1L, json);
        String flowChain = FlowChainBuilder.toFlowChain(jsonGraph);
        System.out.println(flowChain);
    }

    @Test
    public void testSimple() throws IOException {
        testToFlowChain("simple.json");
    }

    @Test
    public void testCLoop() throws IOException {
        testToFlowChain("loop.json");
    }

    @Test
    public void testSwitch() throws IOException {
        testToFlowChain("switch.json");
    }

    @Test
    public void testSwitch2() throws IOException {
        testToFlowChain("switch2.json");
    }
}