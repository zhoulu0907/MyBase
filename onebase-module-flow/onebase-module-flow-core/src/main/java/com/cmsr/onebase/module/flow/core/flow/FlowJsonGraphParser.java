package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.enums.FlowPublishStatusEnum;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.yomahub.liteflow.parser.el.ClassXmlFlowELParser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/2 10:31
 */
@Slf4j
@Setter
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowJsonGraphParser extends ClassXmlFlowELParser {

    public static final String LINE_BREAK = "\n";
    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private GraphFlowCache graphFlowCache;

    @Override
    public String parseCustom() {
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findAllByPublishStatus(FlowPublishStatusEnum.ONLINE.getStatus());
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("flow");
        addNoOpChain(rootElement);
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            try {
                JsonGraph jsonGraph = JsonGraphBuilder.build(flowProcessDO.getProcessDefinition());
                String flowChain = jsonGraph.toFlowChain();
                //
                graphFlowCache.update(flowProcessDO.getId(), jsonGraph);
                //
                String chainId = FlowUtils.toFlowChainId(flowProcessDO.getId());
                Element element = rootElement.addElement("chain").addAttribute("name", chainId);
                element.addText(LINE_BREAK + flowChain);
            } catch (Exception e) {
                log.error("解析流程定义失败: {}", flowProcessDO, e);
            }
        }
        StringWriter buffer = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        format.setIndentSize(4);
        format.setNewlines(true);
        format.setTrimText(false);
        format.setExpandEmptyElements(true);
        XMLWriter writer = new XMLWriter(buffer, format);
        try {
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String xml = buffer.toString();//document.asXML();
        log.info("flow xml: {}", xml);
        return xml;
    }

    /**
     * 为避免报错，添加一个空的链
     * com.yomahub.liteflow.exception.ConfigErrorException:
     * no valid rule config found in rule path [el_xml:com.cmsr.onebase.module.flow.core.flow.FlowJsonGraphParser]
     * at com.yomahub.liteflow.core.FlowExecutor.init(FlowExecutor.java:211)
     *
     * @param rootElement
     */
    private void addNoOpChain(Element rootElement) {
        Element element = rootElement.addElement("chain").addAttribute("name", "no_op_chain");
        element.addText(LINE_BREAK + "SER(noop);");
    }
}
