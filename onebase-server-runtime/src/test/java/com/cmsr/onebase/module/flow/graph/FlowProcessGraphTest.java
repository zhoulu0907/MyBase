package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.graph.JsonGraph;
import com.cmsr.onebase.server.runtime.OneBaseServerRuntimeApplication;
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
@SpringBootTest(classes = OneBaseServerRuntimeApplication.class)
public class FlowProcessGraphTest {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    public void testToFlowChain(Long id) throws IOException {
        FlowProcessDO flowProcessDO = flowProcessRepository.findById(id);
        String json = flowProcessDO.getProcessDefinition();
        JsonGraph jsonGraph = JsonGraph.of(json);
        System.out.println(jsonGraph.toFlowChain());
    }

    @Test
    public void testSimple() throws IOException {
        testToFlowChain(48344014469300224L);
    }
}
