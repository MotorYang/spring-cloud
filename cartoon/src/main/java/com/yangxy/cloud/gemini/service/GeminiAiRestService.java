package com.yangxy.cloud.gemini.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Google Gemini AI 服务 - Redis 版本
 * 使用 Redis 存储会话历史,支持分布式部署
 */
@Slf4j
@Service
public class GeminiAiRestService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${google.ai.api-key}")
    private String apiKey;

    @Value("${google.ai.model:gemini-2.0-flash-exp}")
    private String model;

    // 会话过期时间(秒) - 从配置文件读取
    @Value("${chat.session.ttl:3600}")
    private long sessionTtlSeconds;

    // 每个会话最大消息数
    @Value("${chat.session.max-messages:100}")
    private int maxMessagesPerSession;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Redis Key 前缀
    private static final String SESSION_PREFIX = "chat:session:";
    private static final String SESSION_STATS_KEY = "chat:stats";

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
     * 聊天对话 - Redis 版本
     */
    public String chat(String message, String sessionId) {
        try {
            String redisKey = SESSION_PREFIX + sessionId;

            // 从 Redis 获取会话历史
            List<Map<String, Object>> history = getSessionHistory(redisKey);

            // 如果会话不存在,创建新会话
            if (history == null || history.isEmpty()) {
                history = createNewSession();
                log.info("创建新会话: sessionId={}", sessionId);
                incrementStats("totalSessions");
            } else {
                log.debug("继续现有会话: sessionId={}, 当前消息数={}", sessionId, history.size());
            }

            // 限制消息数量 - 防止单个会话过大
            if (history.size() > maxMessagesPerSession) {
                // 保留系统指令(前2条)和最近的消息
                List<Map<String, Object>> systemMessages = history.subList(0, 2);
                List<Map<String, Object>> recentMessages = history.subList(
                        history.size() - maxMessagesPerSession + 2,
                        history.size()
                );
                history = new ArrayList<>();
                history.addAll(systemMessages);
                history.addAll(recentMessages);
                log.info("会话 {} 消息数超限,已清理早期消息,保留最近{}条",
                        sessionId, maxMessagesPerSession);
            }

            // 添加用户消息
            history.add(createMessage("user", message));

            // 调用 AI API
            String response = generateContentWithHistory(history);

            // 添加 AI 回复
            history.add(createMessage("model", response));

            // 保存到 Redis 并设置过期时间
            saveSessionHistory(redisKey, history);

            // 更新统计信息
            incrementStats("totalMessages");

            log.info("会话 {} 对话成功: 消息数={}, TTL={}秒",
                    sessionId, history.size(), sessionTtlSeconds);

            return response;

        } catch (Exception e) {
            log.error("Redis聊天失败: sessionId={}, message={}", sessionId, message, e);
            throw new RuntimeException("聊天失败: " + e.getMessage());
        }
    }

    /**
     * 从 Redis 获取会话历史
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getSessionHistory(String redisKey) {
        try {
            Object value = redisTemplate.opsForValue().get(redisKey);
            if (value == null) {
                return null;
            }

            // 刷新 TTL
            redisTemplate.expire(redisKey, sessionTtlSeconds, TimeUnit.SECONDS);

            // 转换为 List
            if (value instanceof List) {
                return (List<Map<String, Object>>) value;
            }

            // 如果是 JSON 字符串,解析它
            if (value instanceof String) {
                return objectMapper.readValue(
                        (String) value,
                        new TypeReference<List<Map<String, Object>>>() {}
                );
            }

            log.warn("Redis 中的数据类型不正确: {}", value.getClass());
            return null;

        } catch (Exception e) {
            log.error("从 Redis 获取会话历史失败: key={}", redisKey, e);
            return null;
        }
    }

    /**
     * 保存会话历史到 Redis
     */
    private void saveSessionHistory(String redisKey, List<Map<String, Object>> history) {
        try {
            redisTemplate.opsForValue().set(
                    redisKey,
                    history,
                    sessionTtlSeconds,
                    TimeUnit.SECONDS
            );
            log.debug("会话已保存到Redis: key={}, 消息数={}, TTL={}秒",
                    redisKey, history.size(), sessionTtlSeconds);
        } catch (Exception e) {
            log.error("保存会话历史到 Redis 失败: key={}", redisKey, e);
            throw new RuntimeException("保存会话失败: " + e.getMessage());
        }
    }

    /**
     * 创建新会话
     */
    private List<Map<String, Object>> createNewSession() {
        List<Map<String, Object>> history = new ArrayList<>();
        history.add(createMessage("user", CHAT_SYSTEM_INSTRUCTION));
        history.add(createMessage("model", "Understood. I'm ready to help!"));
        return history;
    }

    /**
     * 清除指定会话
     */
    public void clearChatSession(String sessionId) {
        try {
            String redisKey = SESSION_PREFIX + sessionId;
            Boolean deleted = redisTemplate.delete(redisKey);

            if (Boolean.TRUE.equals(deleted)) {
                log.info("从Redis清除会话: sessionId={}", sessionId);
            } else {
                log.warn("尝试清除不存在的会话: sessionId={}", sessionId);
            }
        } catch (Exception e) {
            log.error("清除会话失败: sessionId={}", sessionId, e);
            throw new RuntimeException("清除会话失败: " + e.getMessage());
        }
    }

    /**
     * 清除所有会话
     */
    public void clearAllSessions() {
        try {
            Set<String> keys = redisTemplate.keys(SESSION_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                Long deletedCount = redisTemplate.delete(keys);
                log.info("清除所有聊天会话: 共{}个", deletedCount);
            } else {
                log.info("没有会话需要清除");
            }
        } catch (Exception e) {
            log.error("清除所有会话失败", e);
            throw new RuntimeException("清除所有会话失败: " + e.getMessage());
        }
    }

    /**
     * 获取活跃会话数量
     */
    public int getActiveChatSessions() {
        try {
            Set<String> keys = redisTemplate.keys(SESSION_PREFIX + "*");
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            log.error("获取活跃会话数量失败", e);
            return 0;
        }
    }

    /**
     * 获取会话统计信息
     */
    public Map<String, Object> getSessionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // 基本统计
            Set<String> keys = redisTemplate.keys(SESSION_PREFIX + "*");
            int totalSessions = keys != null ? keys.size() : 0;
            stats.put("totalSessions", totalSessions);
            stats.put("sessionTtlSeconds", sessionTtlSeconds);
            stats.put("maxMessagesPerSession", maxMessagesPerSession);

            // 消息统计
            int totalMessages = 0;
            int minMessages = Integer.MAX_VALUE;
            int maxMessages = 0;
            Map<String, Long> ttlDistribution = new LinkedHashMap<>();
            ttlDistribution.put("0-15min", 0L);
            ttlDistribution.put("15-30min", 0L);
            ttlDistribution.put("30-60min", 0L);
            ttlDistribution.put("60min+", 0L);

            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    // 获取消息数
                    List<Map<String, Object>> history = getSessionHistory(key);
                    if (history != null) {
                        int msgCount = history.size();
                        totalMessages += msgCount;
                        minMessages = Math.min(minMessages, msgCount);
                        maxMessages = Math.max(maxMessages, msgCount);
                    }

                    // 获取 TTL
                    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                    if (ttl != null && ttl > 0) {
                        long ttlMinutes = ttl / 60;
                        if (ttlMinutes < 15) {
                            ttlDistribution.merge("0-15min", 1L, Long::sum);
                        } else if (ttlMinutes < 30) {
                            ttlDistribution.merge("15-30min", 1L, Long::sum);
                        } else if (ttlMinutes < 60) {
                            ttlDistribution.merge("30-60min", 1L, Long::sum);
                        } else {
                            ttlDistribution.merge("60min+", 1L, Long::sum);
                        }
                    }
                }
            }

            stats.put("totalMessages", totalMessages);
            stats.put("avgMessagesPerSession",
                    totalSessions > 0 ? totalMessages / totalSessions : 0);
            stats.put("minMessagesPerSession", totalSessions > 0 ? minMessages : 0);
            stats.put("maxMessagesPerSession", maxMessages);
            stats.put("ttlDistribution", ttlDistribution);

            // 全局统计(从 Redis Hash 获取)
            Map<Object, Object> globalStats = redisTemplate.opsForHash().entries(SESSION_STATS_KEY);
            stats.put("globalStats", globalStats);

            return stats;

        } catch (Exception e) {
            log.error("获取会话统计失败", e);
            return new HashMap<>();
        }
    }

    /**
     * 增加统计计数
     */
    private void incrementStats(String field) {
        try {
            redisTemplate.opsForHash().increment(SESSION_STATS_KEY, field, 1);
        } catch (Exception e) {
            log.error("更新统计信息失败: field={}", field, e);
        }
    }

    /**
     * 刷新会话 TTL
     */
    public void refreshSessionTtl(String sessionId) {
        try {
            String redisKey = SESSION_PREFIX + sessionId;
            Boolean exists = redisTemplate.hasKey(redisKey);

            if (Boolean.TRUE.equals(exists)) {
                redisTemplate.expire(redisKey, sessionTtlSeconds, TimeUnit.SECONDS);
                log.debug("刷新会话TTL: sessionId={}, TTL={}秒", sessionId, sessionTtlSeconds);
            }
        } catch (Exception e) {
            log.error("刷新会话TTL失败: sessionId={}", sessionId, e);
        }
    }

    /**
     * 检查会话是否存在
     */
    public boolean sessionExists(String sessionId) {
        try {
            String redisKey = SESSION_PREFIX + sessionId;
            return redisTemplate.hasKey(redisKey);
        } catch (Exception e) {
            log.error("检查会话是否存在失败: sessionId={}", sessionId, e);
            return false;
        }
    }

    /**
     * 获取会话详细信息
     */
    public Map<String, Object> getSessionDetails(String sessionId) {
        try {
            String redisKey = SESSION_PREFIX + sessionId;
            Map<String, Object> details = new HashMap<>();

            // 检查会话是否存在
            Boolean exists = redisTemplate.hasKey(redisKey);
            details.put("exists", exists);

            if (Boolean.TRUE.equals(exists)) {
                // 获取会话历史
                List<Map<String, Object>> history = getSessionHistory(redisKey);
                details.put("messageCount", history != null ? history.size() : 0);

                // 获取剩余 TTL
                Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
                details.put("ttlSeconds", ttl);
                details.put("ttlMinutes", ttl != null ? ttl / 60 : 0);

                // 获取最后一条消息
                if (history != null && !history.isEmpty()) {
                    Map<String, Object> lastMessage = history.get(history.size() - 1);
                    details.put("lastMessage", lastMessage);
                }
            }

            return details;

        } catch (Exception e) {
            log.error("获取会话详细信息失败: sessionId={}", sessionId, e);
            return new HashMap<>();
        }
    }

    /**
     * 通用内容生成方法
     */
    private String generateContent(String prompt) {
        try {
            String url = String.format(API_ENDPOINT, model, apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            ));

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
                        "parts", List.of(Map.of("text", msg.get("text")))
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
}