package com.yangxy.cloud.gemini.controller;

import com.yangxy.cloud.common.response.RestResult;
import com.yangxy.cloud.gemini.dto.ChatRequest;
import com.yangxy.cloud.gemini.dto.GenerateBlogRequest;
import com.yangxy.cloud.gemini.dto.GenerateSummaryRequest;
import com.yangxy.cloud.gemini.service.GeminiAiRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AI 控制器
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private GeminiAiRestService geminiAiService;

    /**
     * 生成博客内容
     * POST /cartoon/ai/generate-blog
     */
    @PostMapping("/generate-blog")
    public RestResult<Map<String, Object>> generateBlog(@RequestBody GenerateBlogRequest request) {
        try {
            log.info("收到生成博客请求: title={}, lang={}", request.getTitle(), request.getLang());

            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return RestResult.error("标题不能为空！");
            }

            String content = geminiAiService.generateBlogContent(
                    request.getTitle(),
                    request.getContext(),
                    request.getLang() != null ? request.getLang() : "zh"
            );

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("title", request.getTitle());

            return RestResult.success(response);

        } catch (Exception e) {
            log.error("生成博客内容失败", e);
            return RestResult.build(12001, "生成失败", null);
        }
    }

    /**
     * 生成摘要
     * POST /ai/generate-summary
     *
     * @param request 生成请求
     * @return 生成的摘要
     */
    @PostMapping("/generate-summary")
    public RestResult<Map<String, Object>> generateSummary(@RequestBody GenerateSummaryRequest request) {
        try {
            log.info("收到生成摘要请求: contentLength={}, lang={}",
                    request.getContent() != null ? request.getContent().length() : 0,
                    request.getLang());

            // 参数验证
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return RestResult.error("标题不能为空！");
            }

            String summary = geminiAiService.generateSummary(
                    request.getContent(),
                    request.getLang() != null ? request.getLang() : "zh"
            );

            Map<String, Object> response = new HashMap<>();
            response.put("summary", summary);

            return RestResult.success(response);

        } catch (Exception e) {
            log.error("生成摘要失败", e);
            return RestResult.build(12001, "生成失败", null);
        }
    }

    /**
     * 聊天
     * POST /ai/chat
     *
     * @param request 聊天请求
     * @return AI 回复
     */
    @PostMapping("/chat")
    public RestResult<Map<String, Object>> chat(@RequestBody ChatRequest request) {
        try {
            log.info("收到聊天请求: message={}, sessionId={}",
                    request.getMessage(), request.getSessionId());

            // 参数验证
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return RestResult.error("消息不能为空！");
            }

            // 如果没有 sessionId,生成一个
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
                log.info("生成新会话ID: {}", sessionId);
            }

            // 调用 Redis 服务
            String reply = geminiAiService.chat(request.getMessage(), sessionId);

            Map<String, Object> response = new HashMap<>();
            response.put("reply", reply);
            response.put("sessionId", sessionId);

            return RestResult.success(response);

        } catch (Exception e) {
            log.error("聊天失败", e);
            return RestResult.build(12001, "聊天失败", null);
        }
    }

    /**
     * 清除指定会话
     * DELETE /cartoon/ai/chat/{sessionId}
     */
    @DeleteMapping("/chat/{sessionId}")
    public RestResult<Void> clearChatSession(@PathVariable String sessionId) {
        try {
            log.info("收到清除会话请求: sessionId={}", sessionId);
            geminiAiService.clearChatSession(sessionId);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("清除会话失败: sessionId={}", sessionId, e);
            return RestResult.error("清除会话失败");
        }
    }

    /**
     * 刷新会话TTL
     * POST /cartoon/ai/chat/{sessionId}/refresh
     */
    @PostMapping("/chat/{sessionId}/refresh")
    public RestResult<Void> refreshSession(@PathVariable String sessionId) {
        try {
            log.info("收到刷新会话TTL请求: sessionId={}", sessionId);
            geminiAiService.refreshSessionTtl(sessionId);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("刷新会话TTL失败: sessionId={}", sessionId, e);
            return RestResult.error("刷新会话失败");
        }
    }

    /**
     * 获取会话详情
     * GET /cartoon/ai/chat/{sessionId}
     */
    @GetMapping("/chat/{sessionId}")
    public RestResult<Map<String, Object>> getSessionDetails(@PathVariable String sessionId) {
        try {
            log.info("收到获取会话详情请求: sessionId={}", sessionId);
            Map<String, Object> details = geminiAiService.getSessionDetails(sessionId);
            return RestResult.success(details);
        } catch (Exception e) {
            log.error("获取会话详情失败: sessionId={}", sessionId, e);
            return RestResult.error("获取会话详情失败");
        }
    }

    /**
     * 健康检查
     * GET /cartoon/ai/health-check
     */
    @GetMapping("/health-check")
    public RestResult<Map<String, Object>> healthCheck() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "ok");
            health.put("service", "Gemini AI with Redis");
            health.put("activeSessions", geminiAiService.getActiveChatSessions());
            health.put("timestamp", System.currentTimeMillis());

            return RestResult.success(health);

        } catch (Exception e) {
            log.error("健康检查失败", e);
            return RestResult.error("健康检查失败");
        }
    }

    @GetMapping("/health")
    public RestResult<Map<String, Object>> health() {
        return RestResult.success(Map.of(
                "status", "ok",
                "service", "AI Service",
                "rateLimit", "5 requests per minute per IP"
        ));
    }
}