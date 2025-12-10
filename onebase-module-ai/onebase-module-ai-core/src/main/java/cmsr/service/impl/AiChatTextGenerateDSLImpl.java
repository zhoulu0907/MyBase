package cmsr.service.impl;

import cmsr.service.AiChatService;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("aiChatTextGenerateScreenDSL")
public class AiChatTextGenerateDSLImpl implements AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatTextGenerateDSLImpl.class);

    private final ChatClient dashScopeChatClient;

    @Autowired
    public AiChatTextGenerateDSLImpl(ChatClient.Builder chatClientBuilder, @Value("${text_generate_screen}")String textGenerateScreen) {
        this.dashScopeChatClient = chatClientBuilder
                .defaultSystem(textGenerateScreen)
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
                                .withTemperature(1.0).build()
                )
                .build();
    }

    @Override
    public String simpleChat(String message) {
        log.info(message);
        String content = dashScopeChatClient.prompt(message).call().content();
        return content;
    }
}
