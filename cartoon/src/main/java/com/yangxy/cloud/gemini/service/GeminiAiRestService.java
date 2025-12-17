package com.yangxy.cloud.gemini.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Google Gemini AI 服务（REST API 实现）
 * <p>
 * 直接调用 Google AI API，不依赖 Vertex AI SDK
 * 更简单、更可控
 */
@Slf4j
@Service
public class GeminiAiRestService {

    @Value("${google.ai.api-key}")
    private String apiKey;

    @Value("${google.ai.model:gemini-2.5-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储聊天历史
    private final Map<String, List<Map<String, Object>>> chatHistories = new ConcurrentHashMap<>();

    // Google AI API 端点
    private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    /**
     * 系统指令（用于聊天）
     */
    private static final String CHAT_SYSTEM_INSTRUCTION =
            "You are a helpful, witty, and slightly cartoonish assistant living inside a blog. " +
                    "Keep answers concise and fun. If the user speaks Chinese, reply in Chinese.";

    /**
     * 生成博客内容
     */
    public String generateBlogContent(String title, String context, String lang) {
        try {
            String languageInstruction = "zh".equals(lang)
                    ? "IMPORTANT: Write the entire blog post in Simplified Chinese (简体中文). Keep the tone fun and witty."
                    : "Write the blog post in English. Keep the tone fun and witty.";

            String prompt = String.format("""
                You are a fun, witty, and engaging blog writer for a cartoon-styled tech blog.
                %s
                Write a blog post content (Markdown format) for the title: "%s".
                %s
                Keep it lighthearted, maybe use some emojis, and structure it with H2 headings.
                """,
                    languageInstruction,
                    title,
                    context != null ? "Additional context/instructions: " + context : ""
            );

            return generateContent(prompt);

        } catch (Exception e) {
            log.error("生成博客内容失败: title={}", title, e);
            throw new RuntimeException("生成博客内容失败: " + e.getMessage());
        }
    }

    /**
     * 生成摘要
     */
    public String generateSummary(String content, String lang) {
        try {
            String languageInstruction = "zh".equals(lang)
                    ? "Provide the summary in Simplified Chinese (简体中文)."
                    : "Provide the summary in English.";

            String truncatedContent = content.length() > 1000
                    ? content.substring(0, 1000) + "..."
                    : content;

            String prompt = String.format("""
                Summarize the following blog post in 2 sentences. Make it sound exciting!
                %s
                
                Content:
                %s
                """,
                    languageInstruction,
                    truncatedContent
            );

            return generateContent(prompt);

        } catch (Exception e) {
            log.error("生成摘要失败", e);
            throw new RuntimeException("生成摘要失败: " + e.getMessage());
        }
    }

    /**
     * 聊天对话
     */
    public String chat(String message, String sessionId) {
        try {
            // 获取或创建聊天历史
            List<Map<String, Object>> history = chatHistories.computeIfAbsent(
                    sessionId,
                    k -> {
                        List<Map<String, Object>> newHistory = new ArrayList<>();
                        // 添加系统指令
                        newHistory.add(createMessage("user", CHAT_SYSTEM_INSTRUCTION));
                        newHistory.add(createMessage("model", "Understood. I'm ready to help!"));
                        return newHistory;
                    }
            );

            // 添加用户消息
            history.add(createMessage("user", message));

            // 调用 API
            String response = generateContentWithHistory(history);

            // 添加 AI 回复到历史
            history.add(createMessage("model", response));

            return response;

        } catch (Exception e) {
            log.error("聊天失败: sessionId={}, message={}", sessionId, message, e);
            throw new RuntimeException("聊天失败: " + e.getMessage());
        }
    }

    /**
     * 清除聊天会话
     */
    public void clearChatSession(String sessionId) {
        chatHistories.remove(sessionId);
        log.info("已清除聊天会话: {}", sessionId);
    }

    /**
     * 通用内容生成方法
     */
    private String generateContent(String prompt) {
        try {
            // 构建请求 URL
            String url = String.format(API_ENDPOINT, model, apiKey);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                    Map.of(
                            "parts", List.of(
                                    Map.of("text", prompt)
                            )
                    )
            ));

            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // 解析响应
            return extractTextFromResponse(response.getBody());

        } catch (Exception e) {
            log.error("调用 Gemini API 失败", e);
            throw new RuntimeException("调用 AI API 失败: " + e.getMessage());
        }
    }

    /**
     * 带历史记录的内容生成
     */
    private String generateContentWithHistory(List<Map<String, Object>> history) {
        try {
            String url = String.format(API_ENDPOINT, model, apiKey);

            // 转换历史记录格式
            List<Map<String, Object>> contents = new ArrayList<>();
            for (Map<String, Object> msg : history) {
                contents.add(Map.of(
                        "role", msg.get("role"),
                        "parts", List.of(
                                Map.of("text", msg.get("text"))
                        )
                ));
            }

            Map<String, Object> requestBody = Map.of("contents", contents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return extractTextFromResponse(response.getBody());

        } catch (Exception e) {
            log.error("调用 Gemini API 失败（带历史）", e);
            throw new RuntimeException("调用 AI API 失败: " + e.getMessage());
        }
    }

    /**
     * 从响应中提取文本
     */
    private String extractTextFromResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);

        // 提取文本：candidates[0].content.parts[0].text
        JsonNode candidates = root.path("candidates");
        if (candidates.isEmpty()) {
            throw new RuntimeException("API 响应中没有候选结果");
        }

        JsonNode firstCandidate = candidates.get(0);
        JsonNode content = firstCandidate.path("content");
        JsonNode parts = content.path("parts");

        if (parts.isEmpty()) {
            throw new RuntimeException("API 响应中没有内容部分");
        }

        JsonNode firstPart = parts.get(0);
        String text = firstPart.path("text").asText();

        if (text == null || text.isEmpty()) {
            throw new RuntimeException("API 响应中文本为空");
        }

        return text;
    }

    /**
     * 创建消息对象
     */
    private Map<String, Object> createMessage(String role, String text) {
        Map<String, Object> message = new HashMap<>();
        message.put("role", role);
        message.put("text", text);
        return message;
    }

    /**
     * 获取活跃会话数量
     */
    public int getActiveChatSessions() {
        return chatHistories.size();
    }

    /**
     * 清除所有会话
     */
    public void clearAllSessions() {
        int count = chatHistories.size();
        chatHistories.clear();
        log.info("已清除所有聊天会话，共 {} 个", count);
    }
}