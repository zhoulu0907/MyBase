package com.cmsr.onebase.module.flow.graph;

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

    @Test
    public void testSimple() throws IOException {
        ClassPathResource resource = new ClassPathResource("graphjson/simple.json");
        String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        JsonGraph jsonGraph = JsonGraph.of(json);
        System.out.println(jsonGraph.toFlowChain());
    }

    @Test
    public void testCLoop() throws IOException {
        ClassPathResource resource = new ClassPathResource("graphjson/loop.json");
        String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        JsonGraph jsonGraph = JsonGraph.of(json);
        System.out.println(jsonGraph.toFlowChain());
    }

    @Test
    public void testSwitch() throws IOException {
        ClassPathResource resource = new ClassPathResource("graphjson/switch.json");
        String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        JsonGraph jsonGraph = JsonGraph.of(json);
        System.out.println(jsonGraph.toFlowChain());
    }
}