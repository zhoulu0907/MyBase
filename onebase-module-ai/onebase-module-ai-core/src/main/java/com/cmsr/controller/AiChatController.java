package com.cmsr.controller;

import com.cmsr.common.util.IDUtils;
import com.cmsr.common.util.R;
import com.cmsr.config.ChartDSLConfig;
import com.cmsr.entity.MyAnswer;
import com.cmsr.entity.PicTabAnswer;
import com.cmsr.service.AiChatService;
import com.cmsr.service.QwenVLService;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.google.gson.Gson;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * Ai对话
 * @author mty
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ai/chat")
public class AiChatController {

    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";
    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);

    private final ChatClient dashScopeChatClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("aiChatDataSetDetail")
    private AiChatService aiChatService;

    @Autowired
    @Qualifier("aiChatDataSetList")
    private AiChatService aiChatDataSetService;

    @Autowired
    @Qualifier("aiChatGenerateDSL")
    private AiChatService aiChatDataDSLService;

    @Autowired
    @Qualifier("aiChatTextGenerateScreenDSL")
    private AiChatService aiChatTextGenerateDSLImpl;

    @Value("${dslTemplate}")
    private String dslTemplate;

    @Value("${chartConfig}")
    private String charfig;

    @Autowired
    private ChartDSLConfig chartDSLConfig;

    @Value("${data_address}")
    private String dataServiceUrl;

    @Value("${rlzyDSL}")
    private String rlzyDSL;

    //@Value("${ylyqsjDSL}")
    private String yqjgDSL;

    @Value("${dzswDSL}")
    private String dzswDSL;

    @Value("${znkfDSL}")
    private String znkfDSL;

    @Value("${kjrjDSL}")
    private String kjrjDSL;

    //@Value("${getSetData}")
    // private String getSetData;

    @Autowired
    private QwenVLService qwenService;

    @Autowired
    public AiChatController(ChatClient.Builder chatClientBuilder) {

        this.dashScopeChatClient = chatClientBuilder
                .defaultSystem(DEFAULT_PROMPT)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withModel("qwen-turbo")
                                .withTemperature(1.0)
                                .build())
                .build();
    }

    @PostMapping(path = "/aibuild/chatIntention")
    public Object dataSet(@RequestBody Map<String, Object> msgReqMap, HttpServletResponse response) {

        log.info("请求参数：" + msgReqMap);

        Gson gson = new Gson();
        System.out.println(charfig);
        String message = (String) msgReqMap.get("message");
        // dashScopeChatClient.opo
        // 用户输入
        // UserMessage userMessage = new UserMessage(message);
        String content = aiChatDataSetService.simpleChat(message);
        content = content.replace("json", "").replace("```", "");
        log.info("第一次调用大模型问客户意图：" + content);
        // Gson gson = new Gson();
        MyAnswer answer = gson.fromJson(content, MyAnswer.class);

        // 2.根据大模型返回的不同场景码，判断用户是要返回大屏，还是普通会话
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("leaf", true);
        Map<String, Object> resMap = null;
        System.out.println(dataServiceUrl + "/datasetTreeN/leaf");
        Map dataResMap = restTemplate.postForObject(dataServiceUrl + "/datasetTreeN/leaf", reqMap, Map.class);

        switch (answer.getIntention()) {
            case "1":
                // dataResMap = restTemplate.postForObject("http://127.0.0.1:8100/de2api/datasetTreeN/leaf", reqMap, Map.class);
                List dataResList = (List) dataResMap.get("data");
                StringBuilder answerMsg = new StringBuilder("当前系统内的**数据集**有：\n");
                if (dataResList.size() != 0) {
                    for (int i = 0; i < dataResList.size(); i++) {
                        Map map = (Map) dataResList.get(i);
                        answerMsg.append(i + 1).append(".").append(map.get("name")).append("\n");
                    }
                }
                System.out.println(answerMsg);
                answer.setAnswer(String.valueOf(answerMsg));
                Map map = new HashMap();
                map.put("intention", answer.getIntention());
                map.put("reason", answer.getReason());
                map.put("reliability", answer.getReliability());
                map.put("answer", answer.getAnswer());
                if ("0".equals(String.valueOf(dataResMap.get("code"))) && dataResMap.get("data") != null) {
                    map.put("data", dataResMap.get("data"));
                }
                resMap = new HashMap();
                resMap.put("code", 0);
                resMap.put("msg", null);
                resMap.put("data", map);
                log.info("返回参数：" + gson.toJson(resMap));
                return resMap;
            case "2":
                // dataResMap = restTemplate.postForObject("http://127.0.0.1:8100/de2api/datasetTreeN/leaf", reqMap, Map.class);
                List listAttrs = null;
                if (!CollectionUtils.isEmpty(dataResMap)) {
                    listAttrs = (List) dataResMap.get("data");
                }
                // Map msgMap = gson.fromJson(message, Map.class);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("数据集列表(JSON)：").append(listAttrs).append("用户问题：").append(message);
                String msg = stringBuilder.toString();
                String strAnswer = aiChatService.simpleChat(msg);
                // log.info("情境2：ai返回" + strAnswer);
                strAnswer = strAnswer.replace("```", "").replace("json", "");
                log.info("情境2：调整后返回" + strAnswer);
                if (StringUtils.isNotEmpty(strAnswer) && strAnswer.contains("解释：")) {
                    strAnswer = strAnswer.substring(0, strAnswer.indexOf("解释：")).trim();
                }
                if (StringUtils.isNotEmpty(strAnswer) && strAnswer.contains("[]")) {
                    Map renturnMap = new HashMap();
                    renturnMap.put("data", "");
                    renturnMap.put("intention", answer.getIntention());
                    renturnMap.put("reason", "意图不明，请明确意图再试一下");
                    renturnMap.put("reliability", answer.getReliability());
                    renturnMap.put("answer", "意图不明，没有符合意图的数据集详情，请明确意图再试一下");
                    resMap = new HashMap();
                    resMap.put("code", 0);
                    resMap.put("msg", null);
                    resMap.put("data", renturnMap);
                    return resMap;
                }
                strAnswer = strAnswer.replace("\'", "");
                List list = gson.fromJson(strAnswer, List.class);
                Map detailMaps = new HashMap();
                // detailMaps.put("answers", list);
                List<Long> ids = new ArrayList<>();
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    Map next = (Map) iterator.next();
                    ids.add(Long.valueOf((String) next.get("id")));
                }
                System.out.println(ids);

                Map detailMap = restTemplate.postForObject(dataServiceUrl + "/datasetTreeN/dsDetails", ids, Map.class);
                Map sumMap = (Map) detailMap.get("data");
                List list2 = (List) sumMap.get("list");
                detailMaps.put("data", list2);
                detailMaps.put("intention", answer.getIntention());
                detailMaps.put("reason", answer.getReason());
                detailMaps.put("reliability", answer.getReliability());

                StringBuilder answerSb = new StringBuilder();

                if (list2.size() != 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        Map ansMap = (Map) list2.get(i);
                        List dimensions = (List) ansMap.get("dimension");
                        StringBuilder dimension = new StringBuilder();
                        for (Object str : dimensions) {
                            dimension.append("+ ").append(str).append("\n");
                        }
                        List metrics = (List) ansMap.get("metrics");
                        StringBuilder metric = new StringBuilder();
                        for (Object str : metrics) {
                            metric.append("+ ").append(str).append("\n");
                        }
                        answerSb.append("**数据集：** ").append(ansMap.get("datasetName")).append("\n").append("**意图分析：** ")
                                .append(answer.getReason()).append("\n").append("**维度：**\n").append(dimension)
                                .append("\n**指标：**\n").append(metric);
                    }
                }

                detailMaps.put("answer", answerSb.toString());

                resMap = new HashMap();
                resMap.put("code", 0);
                resMap.put("msg", null);
                resMap.put("data", detailMaps);

                log.info("返回参数：" + gson.toJson(resMap));
                return resMap;
            case "3":
                // 根据sceneType判断
                //  'project_list' : 项目列表页 不允许生成图表
                // 'screen_designer': 数据大屏页 可以生成图表
                String projectList = (String) msgReqMap.get("sceneType");
                if (StringUtils.isNotEmpty(projectList) && projectList.equals("project_list")) {
                    Map renturnMap = new HashMap();
                    renturnMap.put("intention", answer.getIntention());
                    renturnMap.put("reason", "项目列表页，不允许生成图表");
                    renturnMap.put("reliability", answer.getReliability());
                    renturnMap.put("answer", "项目列表页，不允许生成图表");
                    resMap = new HashMap();
                    resMap.put("code", 0);
                    resMap.put("msg", null);
                    resMap.put("data", renturnMap);
                    return resMap;
                }

                List IdsN = new ArrayList();
                List listIds = (List) dataResMap.get("data");
                Iterator dslIterator = listIds.iterator();
                while (dslIterator.hasNext()) {
                    Map next = (Map) dslIterator.next();
                    IdsN.add(next.get("id"));
                }
                Map detailDslMap = restTemplate.postForObject(dataServiceUrl + "/datasetTreeN/dsDetails", IdsN, Map.class);
                Map dataMap = (Map) detailDslMap.get("data");
                System.out.println("#####" + dataMap);
                List data = (List) dataMap.get("list");
                System.out.println(data);
                String json = gson.toJson(data);
                List<Map<String, String>> charfigs = gson.fromJson(charfig, List.class);
                List<Map<String, String>> simplifiedList = charfigs.stream()
                        .map(item -> {
                            Map<String, String> newItem = new HashMap<>();
                            newItem.put("key", item.get("key"));
                            newItem.put("title", item.get("title"));
                            return newItem;
                        })
                        .collect(Collectors.toList());
                System.out.println(simplifiedList);
                String charfigStr = gson.toJson(simplifiedList);
                System.out.println(charfigStr);
                StringBuilder datasetSb = new StringBuilder();
                // Map messageMap = gson.fromJson(message, Map.class);
                datasetSb.append("[数据集列表]：").append(json).append("[图表类型列表]：").append(charfigStr).append("[用户问题]：").append(message);
                String dataSetmsg = datasetSb.toString();
                String chat = aiChatDataDSLService.simpleChat(dataSetmsg);
                chat = chat.replace("```", "").replace("json", "");
                System.out.println(chat);
                log.info("情境3，ai返回：" + chat);
                if (StringUtils.isEmpty(chat)) {
                    Map respMap = new HashMap();
                    respMap.put("intention", answer.getIntention());
                    respMap.put("reason", "");
                    respMap.put("reliability", answer.getReliability());
                    respMap.put("answer", "系统繁忙，请稍后再试！");
                    respMap.put("data", new Object());
                    respMap.put("generateSuccess", "false");
                    resMap = new HashMap();
                    resMap.put("code", 0);
                    resMap.put("msg", null);
                    resMap.put("data", respMap);
                    return resMap;
                }

                PicTabAnswer picTabAnswer = gson.fromJson(chat, PicTabAnswer.class);
                if (StringUtils.isNotEmpty(chat) && (StringUtils.isEmpty(picTabAnswer.getDatasetName()) ||
                        StringUtils.isEmpty(picTabAnswer.getDatasetId()))) {
                    Map respMap = new HashMap();
                    respMap.put("intention", answer.getIntention());
                    respMap.put("reason", answer.getReason());
                    respMap.put("reliability", answer.getReliability());
                    respMap.put("answer", picTabAnswer.getReason());
                    respMap.put("data", null);
                    respMap.put("generateSuccess", false);
                    resMap = new HashMap();
                    resMap.put("code", 0);
                    resMap.put("msg", null);
                    resMap.put("data", respMap);
                    return resMap;
                }

                Map type = new HashMap();
                type.put("BarCommon", "bar");
                type.put("LineCommon", "line");
                type.put("PieCommon", "pie");
                type.put("Funnel", "funnel");
                Map typeMap = new HashMap();
                typeMap.put("type", type.get(picTabAnswer.getChartKey()));
                Map listByDQMap = restTemplate.postForObject(dataServiceUrl + "/chart/listByDQ/" + picTabAnswer.getDatasetId() + "/123", typeMap, Map.class);
                Map listByDQ = (Map) listByDQMap.get("data");
                List<Map> dimensions = (List<Map>) listByDQ.get("dimensionList");
                List<Map> metrics = (List<Map>) listByDQ.get("quotaList");
                // List list2 = dimensions.stream().filter(dimension ->
                //        picTabAnswer.getDimension().equals(dimension.getName())).toList();
                //
                // List list1 = metrics.stream().filter(dimension ->
                //        picTabAnswer.getMetrics().equals(dimension.getName())).toList();
                Iterator<Map> dimenMetricEntityIterator = dimensions.iterator();
                List xAxisIds = new ArrayList();
                List yAxisIds = new ArrayList();
                List<Map> dimensionList = new ArrayList<>();
                Map dimensionMap = null;
                while (dimenMetricEntityIterator.hasNext()) {
                    Map next = dimenMetricEntityIterator.next();

                    if (!picTabAnswer.getDimension().equals(next.get("name"))) {
                        dimenMetricEntityIterator.remove();
                    } else {
                        xAxisIds.add(next.get("id"));
                        dimensionMap = new HashMap();
                        dimensionMap.put("id", next.get("id"));
                        dimensionMap.put("datasourceId", next.get("datasourceId"));
                        dimensionMap.put("datasetTableId", next.get("datasetTableId"));
                        dimensionMap.put("datasetGroupId", next.get("datasetGroupId"));
                        dimensionMap.put("name", next.get("name"));
                        dimensionMap.put("dataeaseName", next.get("dataeaseName"));
                        dimensionMap.put("deType", next.get("deType"));
                        dimensionList.add(dimensionMap);
                    }
                }

                Iterator<Map> metricEntityIterator = metrics.iterator();
                List<Map> metricList = new ArrayList<>();
                Map metricMap = null;
                while (metricEntityIterator.hasNext()) {
                    Map next = metricEntityIterator.next();
                    if (!picTabAnswer.getMetrics().equals(next.get("name"))) {
                        metricEntityIterator.remove();
                    } else {
                        yAxisIds.add(next.get("id"));
                        metricMap = new HashMap();
                        metricMap.put("id", next.get("id"));
                        metricMap.put("datasourceId", next.get("datasourceId"));
                        metricMap.put("datasetTableId", next.get("datasetTableId"));
                        metricMap.put("datasetGroupId", next.get("datasetGroupId"));
                        metricMap.put("name", next.get("name"));
                        metricMap.put("dataeaseName", next.get("dataeaseName"));
                        metricMap.put("deType", next.get("deType"));
                        metricMap.put("summary", next.get("summary"));
                        metricMap.put("extField", next.get("extField"));
                        metricList.add(metricMap);
                    }
                }
                // dimensions.stream().filter(dimension->dimensions.get(""))
                // Map getDataMap = new HashMap();
                // getDataMap.put("tableId", picTabAnswer.getDatasetId());
                // getDataMap.put("xAxis", dimensionList);
                // getDataMap.put("yAxis", metricList);
                // getDataMap.put("drillFields", new ArrayList<>());
                // getDataMap.put("extLabel", new ArrayList<>());
                // getDataMap.put("extTooltip", new ArrayList<>());
                // System.out.println(gson.toJson(getDataMap));
                //
                // Map getSetDataMap = restTemplate.postForObject(dataServiceUrl+"/chartDataN/getSetData", getDataMap, Map.class);

                // gson.fromJson(getSetData,Map);
                // 替换dslMap内容

                // Map type = new HashMap();
                // type.put("BarCommon", "bar");
                // type.put("LineCommon", "line");
                // type.put("PieCommon", "pie");
                // type.put("Funnel", "funnel");

                if ("BarCommon".equals(picTabAnswer.getChartKey())) {
                    dslTemplate = chartDSLConfig.getCharBarDSL();
                } else if ("LineCommon".equals(picTabAnswer.getChartKey())) {
                    dslTemplate = chartDSLConfig.getCharLineDSL();
                } else if ("PieCommon".equals(picTabAnswer.getChartKey())) {
                    dslTemplate = chartDSLConfig.getCharPieDSL();
                } else if ("Funnel".equals(picTabAnswer.getChartKey())) {
                    dslTemplate = chartDSLConfig.getCharFunnelDSL();
                }

                Map dslMap = gson.fromJson(dslTemplate, Map.class);
                String id = "id_" + IDUtils.randomID(13);
                // String id = "id_" + UUID.randomUUID();
                Map paramMap = (Map) msgReqMap.get("attrParam");
                if (!CollectionUtils.isEmpty(paramMap)) {
                    Map attrMap = (Map) dslMap.get("attr");
                    id = (String) paramMap.get("id");
                    attrMap.put("x", paramMap.get("x"));
                    attrMap.put("y", paramMap.get("y"));
                    attrMap.put("w", paramMap.get("w"));
                    attrMap.put("h", paramMap.get("h"));
                    dslMap.put("attr", attrMap);
                }
                // Iterator<Map<String, String>> iterator1 = charfigs.stream().iterator();
                // while (iterator1.hasNext()) {
                //    Map<String, String> next = iterator1.next();
                //    if (next.get("key").equals(picTabAnswer.getChartKey())) {
                //        dslMap.put("chartConfig", next);
                //    }
                //}
                Map datasetMap = new HashMap();
                datasetMap.put("xAxis", dimensionList);
                datasetMap.put("yAxis", metricList);
                datasetMap.put("selectedDatasetId", picTabAnswer.getDatasetId());
                datasetMap.put("dimensionValue", xAxisIds);
                datasetMap.put("quotaValue", yAxisIds);
                dslMap.put("id", id);
                dslMap.put("datasetOptions", datasetMap);

                // System.out.println(getDataMap);

                Map resDataMap = new HashMap();

                resDataMap.put("intention", answer.getIntention());
                resDataMap.put("reason", answer.getReason());
                resDataMap.put("reliability", answer.getReliability());
                resDataMap.put("answer", answer.getAnswer() == null ? "图表生成成功\n" + answer.getReason() : answer.getAnswer());
                resDataMap.put("data", picTabAnswer);
                resDataMap.put("generateSuccess", true);
                resDataMap.put("generateDSL", dslMap);

                resMap = new HashMap();

                resMap.put("code", 0);
                resMap.put("msg", null);
                resMap.put("data", resDataMap);

                log.info("返回参数：", gson.toJson(resMap));

                return resMap;
            case "4":
                String simpledChat = aiChatTextGenerateDSLImpl.simpleChat(message);
                simpledChat = simpledChat.replace("json", "").replace("```", "");
                MyAnswer myAnswer = gson.fromJson(simpledChat, MyAnswer.class);
                String intention = myAnswer.getIntention();
                R result = new R();
                if (intention.equals("gs_znkf")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("textGenerateDSL", gson.fromJson(znkfDSL, Map.class));
                    resultMap.put("intention", answer.getIntention());
                    resultMap.put("reason", answer.getReason());
                    resultMap.put("reliability", myAnswer.getReliability());

                    resultMap.put("answer", myAnswer.getReason() != null ? "大屏生成成功\n" + myAnswer.getReason() : answer.getAnswer());
                    resultMap.put("generateSuccess", true);

                    result.put("data", resultMap);
                } else if (intention.equals("kj_kjrj")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("textGenerateDSL", gson.fromJson(kjrjDSL, Map.class));
                    resultMap.put("intention", answer.getIntention());
                    resultMap.put("reason", answer.getReason());
                    resultMap.put("reliability", myAnswer.getReliability());

                    resultMap.put("answer", myAnswer.getReason() != null ? "大屏生成成功\n" + myAnswer.getReason() : answer.getAnswer());
                    resultMap.put("generateSuccess", true);

                    result.put("data", resultMap);
                } else if (intention.equals("yl_yqsj")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("textGenerateDSL", gson.fromJson(yqjgDSL, Map.class));
                    resultMap.put("intention", answer.getIntention());
                    resultMap.put("reason", answer.getReason());
                    resultMap.put("reliability", myAnswer.getReliability());

                    resultMap.put("answer", myAnswer.getReason() != null ? "大屏生成成功\n" + myAnswer.getReason() : answer.getAnswer());
                    resultMap.put("generateSuccess", true);

                    result.put("data", resultMap);
                } else if (intention.equals("ds_yysj")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("textGenerateDSL", gson.fromJson(dzswDSL, Map.class));
                    resultMap.put("intention", answer.getIntention());
                    resultMap.put("reason", answer.getReason());
                    resultMap.put("reliability", myAnswer.getReliability());

                    resultMap.put("answer", myAnswer.getReason() != null ? "大屏生成成功\n" + myAnswer.getReason() : answer.getAnswer());
                    resultMap.put("generateSuccess", true);

                    result.put("data", resultMap);
                } else if (intention.equals("gs_rlzy")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("textGenerateDSL", gson.fromJson(rlzyDSL, Map.class));
                    resultMap.put("intention", answer.getIntention());
                    resultMap.put("reason", answer.getReason());
                    resultMap.put("reliability", myAnswer.getReliability());

                    resultMap.put("answer", myAnswer.getReason() != null ? "大屏生成成功\n" + myAnswer.getReason() : answer.getAnswer());
                    resultMap.put("generateSuccess", true);

                    result.put("data", resultMap);
                } else {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("intention", answer.getIntention());
                    resultMap.put("reason", answer.getReason());
                    resultMap.put("reliability", myAnswer.getReliability());

                    resultMap.put("answer", "暂不支持快速创建此类数据大屏。\n" + myAnswer.getReason());
                    resultMap.put("generateSuccess", false);

                    result.put("data", resultMap);
                }
                return result;

            case "5":

                String imageUrl = (String) msgReqMap.get("imageUrl");
                String prompt = (String) msgReqMap.get("message");
                String res = qwenService.generateDescription(imageUrl, prompt);
                res = res.replace("```", "").replace("json", "");
                Map resMap2 = new HashMap();
                resMap2.put("intention", answer.getIntention());
                resMap2.put("reason", answer.getReason());
                resMap2.put("reliability", answer.getReliability());
                resMap2.put("answer", gson.fromJson(res, Map.class));

                return new R().put("data", resMap2);
            // resMap = new HashMap();
            // resMap.put("code", 0);
            // resMap.put("msg", null);
            // resMap.put("data", answer);
            // return resMap;
            case "6":
                resMap = new HashMap();
                resMap.put("code", 0);
                resMap.put("msg", null);
                resMap.put("data", answer);
                return resMap;
        }
        // 3.返回大屏数据，调大屏数据接口
        // 4.普通会话，流式返回普通会话
        return null;
    }

    @PostMapping(path = "/simple/chatPicIntention")
    public Object aiChatPicIntention(@RequestBody Map map) {
//        String description = systemPromt.getDescription();
//        System.out.println(description);

        String message = (String) map.get("message");
        String url = (String) map.get("url");
        log.info(message);
        String content = dashScopeChatClient.prompt(message).call().content();

        R result = new R();
        result.put("data", content);
        return result;
    }

    private List filterDataTree(List list, List nodes) {
        Iterator iterator = list.iterator();
        Map map = null;
        while (iterator.hasNext()) {
            map = new HashMap();
            Map next = (Map) iterator.next();
            map.put("id", next.get("id"));
            map.put("name", next.get("name"));
            nodes.add(map);
            List children = (List) next.get("children");
            if (children != null) {
                filterDataTree(children, nodes);
            }
        }
        return nodes;
    }


    /**
     * 简单调用
     *
     * @param message
     * @return
     */
    @PostMapping(path = "/simple/chat")
    public Object simpleChat(@RequestBody String message) {
//        String description = systemPromt.getDescription();
//        System.out.println(description);
        log.info(message);
        String content = dashScopeChatClient.prompt(message).call().content();

        R result = new R();
        result.put("data", content);
        return result;
    }

    /**
     * ChatClient 流式调用
     */
    @PostMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Object streamChat(@RequestBody String message, HttpServletResponse response) {

        log.info(message);

        List<String> msgList = new ArrayList<>();
        msgList.add(message);

        response.setCharacterEncoding("UTF-8");
//        String content = dashScopeChatClient.prompt(message).call().content();
        Flux<String> content = dashScopeChatClient.prompt(message).stream().content();
        return content.map((s) -> {
            log.info(s);
            return s;
        });
//        return content;
    }

    /**
     * ChatClient 流式调用
     */
    @PostMapping(value = "/stream/chatN", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatN(@RequestBody String message, HttpServletResponse response) {

        log.info(message);

        response.setCharacterEncoding("UTF-8");
        Flux<String> content = dashScopeChatClient.prompt(message).stream().content();
        return content.map(data -> data.replace("", "\\n")) // 暂时替换换行符以避免问题
                .map((String s) -> {
                    log.info(s);
                    return s;
                });
    }

    /**
     * ChatClient 流式调用
     */
    @PostMapping(value = "/stream/chatResp", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MyAnswer> streamChat1(@RequestBody String message, HttpServletResponse response) {

        log.info(message);
        response.setCharacterEncoding("UTF-8");
        Flux<String> content = dashScopeChatClient.prompt(message).stream().content();
        return content.map(data -> data.replace("", "\\n")) // 暂时替换换行符以避免问题
                .map((String s) -> {
                    log.info(s);
                    MyAnswer answer = new MyAnswer();
                    answer.setAnswer(s);
                    // answer.setId(String.valueOf(UUID.randomUUID()));
                    return answer;
                });
    }

    /**
     * ChatClient 使用自定义的 Advisor 实现功能增强.
     *
     * @param response
     * @param id
     * @param message
     * @return
     */
    @PostMapping(value = "/advisor/chat/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> advisorChat(
            HttpServletResponse response,
            @PathVariable String id,
            @RequestBody String message) {

        log.info("" + id + "" + message);

        response.setCharacterEncoding("UTF-8");

        return this.dashScopeChatClient.prompt(message)
                .advisors(
                        a -> a
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                ).stream().content();
    }

}
