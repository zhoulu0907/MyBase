package com.cmsr.onebase.module.flow.graph;

import com.cmsr.onebase.module.flow.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessDO;
import com.yomahub.liteflow.parser.el.ClassXmlFlowELParser;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/2 10:31
 */
@Slf4j
@Component
public class GraphFlowELParser extends ClassXmlFlowELParser {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    public GraphFlowELParser() {
        super();
    }

    @Override
    public String parseCustom() {
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findAll();
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("flow");
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            try {
                JsonGraph jsonGraph = JsonGraph.of(flowProcessDO.getProcessDefinition());
                String flowChain = jsonGraph.toFlowChain();
                Element element = rootElement.addElement("chain").addAttribute("name", "chain" + flowProcessDO.getId());
                element.addCDATA(flowChain);
            } catch (Exception e) {
                log.error("解析流程定义失败：{}", e);
            }
        }
        String xml = document.asXML();
        log.debug("xml: {}", xml);
        return xml;
    }
}
