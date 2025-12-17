package com.yangxy.cloud.gemini.controller;

import com.yangxy.cloud.common.response.RestResult;
import com.yangxy.cloud.gemini.dto.ChatRequest;
import com.yangxy.cloud.gemini.dto.GenerateBlogRequest;
import com.yangxy.cloud.gemini.dto.GenerateSummaryRequest;
import com.yangxy.cloud.gemini.service.GeminiAiRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AI 功能 Controller
 *
 * 提供博客生成、摘要生成和聊天功能
 *
 * 注意：所有接口都受到限流保护（每个 IP 每分钟最多 5 次请求）
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiAiRestService geminiAiService;

    /**
     * 生成博客内容
     * POST /ai/generate-blog
     *
     * @param request 生成请求
     * @return 生成的博客内容（Markdown 格式）
     */
    @PostMapping("/generate-blog")
    public RestResult<Map<String, Object>> generateBlog(@RequestBody GenerateBlogRequest request) {
        try {
            log.info("收到生成博客请求: title={}, lang={}", request.getTitle(), request.getLang());

            // 参数验证
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
                return RestResult.error("标题不能为空！");
            }

            // 如果没有 sessionId，生成一个
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            String reply = geminiAiService.chat(request.getMessage(), sessionId);

            Map<String, Object> response = new HashMap<>();
            response.put("reply", reply);
            response.put("sessionId", sessionId);

            return RestResult.success(response);

        } catch (Exception e) {
            log.error("聊天失败", e);
            return RestResult.build(12001, "生成失败", null);
        }
    }

    /**
     * 清除聊天会话
     * DELETE /ai/chat/{sessionId}
     *
     * @param sessionId 会话ID
     * @return 成功消息
     */
    @DeleteMapping("/chat/{sessionId}")
    public RestResult<String> clearChatSession(@PathVariable String sessionId) {
        try {
            geminiAiService.clearChatSession(sessionId);
            return RestResult.success("会话已清除");
        } catch (Exception e) {
            log.error("清除会话失败", e);
            return RestResult.build(12002,"清除会话失败", null);
        }
    }

    /**
     * 健康检查
     * GET /ai/health
     */
    @GetMapping("/health")
    public RestResult<Map<String, Object>> health() {
        return RestResult.success(Map.of(
                "status", "ok",
                "service", "AI Service",
                "rateLimit", "5 requests per minute per IP"
        ));
    }
}

