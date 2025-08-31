package com.example.ai.service;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class QwenChatModel implements OpenAiApi.ChatModel {

    private final WebClient webClient;
    private final String apiKey;

    public QwenChatModel(WebClient.Builder webClientBuilder,
                         @Value("${spring.ai.qwen.api-key}") String apiKey) {
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        // 实现调用通义千问 API 的逻辑
        // 参考：https://help.aliyun.com/zh/dashscope/developer-reference/quick-start
        String content = webClient.post()
                .uri("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(buildRequestBody(prompt))
                .retrieve()
                .bodyToMono(Map.class)
                .block(); // 简化示例，生产环境用 reactive

        return new ChatResponse(List.of(new Generation(content.toString())));
    }

    private Object buildRequestBody(Prompt prompt) {
        // 构造 DashScope API 请求体
        return Map.of(
                "model", "qwen-plus",
                "input", Map.of("messages", prompt.getInstructions())
        );
    }

    @Override
    public StreamResponse stream(Prompt prompt) {
        throw new UnsupportedOperationException("流式暂未实现");
    }
}
