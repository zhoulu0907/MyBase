package cmsr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QwenVLService {

    @Autowired
    private RestClient restClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${pic_tab_generate}")
    private String systemPrompt;

    public String generateDescription(String imageUrl, String prompt) {

        Map systemPromptMap = new HashMap();

        systemPromptMap.put("role","system");
        systemPromptMap.put("content",systemPrompt);

        Map imageMap = new HashMap<>();
        Map promptMap = new HashMap();

        imageMap.put("image",imageUrl);
        promptMap.put("text",prompt);

        Map userMp = new HashMap();

        userMp.put("role","user");
        userMp.put("content",Arrays.asList(imageMap,promptMap));

        List<Object> list = Arrays.asList(systemPromptMap, userMp);
        // 构建请求体
        Map<String, Object> request = Map.of(
                "model", "qwen-vl-max",
                "input", Map.of(
                        "messages", list
                ),
                "parameters", Map.of(
                        "max_tokens", 1024
                )
        );

        try {
            String jsonBody = objectMapper.writeValueAsString(request);

            // 发送请求
            String response = restClient.post().body(jsonBody).retrieve().body(String.class);
            // 解析响应
            JsonNode root = objectMapper.readTree(response);

            String text = root.path("output")
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText();
            return text;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 处理失败", e);
        }
    }
}
