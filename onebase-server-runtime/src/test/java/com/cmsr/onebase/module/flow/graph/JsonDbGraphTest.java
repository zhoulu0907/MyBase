package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.graph.FlowChainBuilder;
import com.cmsr.onebase.module.flow.core.graph.FlowGraphBuilder;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @Author：huangjie
 * @Date：2025/9/1 12:11
 */
@Setter
@SpringBootTest
public class JsonDbGraphTest {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    public void testToFlowChain(Long id) throws IOException {
        FlowProcessDO flowProcessDO = flowProcessRepository.getById(id);
        String json = flowProcessDO.getProcessDefinition();
        JsonGraph jsonGraph = FlowGraphBuilder.build(json);
        String flowChain = FlowChainBuilder.toFlowChain(jsonGraph);
        System.out.println(flowChain);
    }

    @Test
    public void testSimple() throws IOException {
        testToFlowChain(114994365031546880L);
    }
}
