package com.example.ai.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final StreamingChatClient streamingChatModel;

    // 系统提示词模板
    private static final String SYSTEM_PROMPT = """
        你是一个专业的电商客服助手，回答要简洁、友好。
        如果不知道答案，就说“抱歉，我暂时无法回答”。
        """;


    public String chat(String message) {
        // 使用系统提示词模板
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(SYSTEM_PROMPT);

        Prompt prompt = new Prompt(message);

        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    // 流式响应（SSE）
    public Flux<String> chatStream(String message) {
        // 创建提示词
        PromptTemplate systemPromptTemplate = new PromptTemplate(SYSTEM_PROMPT);
        Prompt prompt = Prompt.builder()
                .system(systemPromptTemplate.create())
                .user(message)
                .build();

        // 使用 streamingChatModel.stream(prompt)
        return streamingChatModel.stream(prompt)
                .map(response -> response.getResult().getOutput().getContent());
    }
}
