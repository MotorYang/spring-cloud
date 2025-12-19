package com.yangxy.cloud.gemini.controller;

import com.yangxy.cloud.common.response.RestResult;
import com.yangxy.cloud.gemini.service.GeminiAiRestService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Redis 聊天会话管理控制器
 * 提供会话监控、管理和统计功能
 */
@Slf4j
@RestController
@RequestMapping("/ai-admin")
public class ChatSessionAdminController {

    @Resource
    private GeminiAiRestService geminiAiService;

    /**
     * 获取会话统计信息
     * GET /cartoon/ai/admin/stats
     *
     * 返回:
     * - totalSessions: 当前活跃会话数
     * - totalMessages: 所有会话的总消息数
     * - avgMessagesPerSession: 平均每个会话的消息数
     * - ttlDistribution: TTL 分布情况
     * - globalStats: 全局统计(总会话数、总消息数等)
     */
    @GetMapping("/stats")
    public RestResult<Map<String, Object>> getSessionStats() {
        try {
            Map<String, Object> stats = geminiAiService.getSessionStats();
            return RestResult.success(stats);
        } catch (Exception e) {
            log.error("获取会话统计失败", e);
            return RestResult.error("获取会话统计失败");
        }
    }

    /**
     * 获取活跃会话数量
     * GET /cartoon/ai/admin/count
     */
    @GetMapping("/count")
    public RestResult<Integer> getActiveSessionCount() {
        try {
            int count = geminiAiService.getActiveChatSessions();
            return RestResult.success(count);
        } catch (Exception e) {
            log.error("获取活跃会话数量失败", e);
            return RestResult.error("获取活跃会话数量失败");
        }
    }

    /**
     * 获取指定会话的详细信息
     * GET /cartoon/ai/admin/sessions/{sessionId}
     */
    @GetMapping("/sessions/{sessionId}")
    public RestResult<Map<String, Object>> getSessionInfo(@PathVariable String sessionId) {
        try {
            Map<String, Object> info = geminiAiService.getSessionDetails(sessionId);
            return RestResult.success(info);
        } catch (Exception e) {
            log.error("获取会话信息失败: sessionId={}", sessionId, e);
            return RestResult.error("获取会话信息失败");
        }
    }

    /**
     * 清除所有会话
     * DELETE /cartoon/ai/admin/sessions
     * 注意: 这会清除 Redis 中所有聊天会话,谨慎使用!
     */
    @DeleteMapping("/sessions")
    public RestResult<Void> clearAllSessions() {
        try {
            log.warn("执行清除所有会话操作");
            geminiAiService.clearAllSessions();
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("清除所有会话失败", e);
            return RestResult.error("清除所有会话失败");
        }
    }

    /**
     * 检查会话是否存在
     */
    @GetMapping("/sessions/exists/{sessionId}")
    public RestResult<Boolean> sessionExists(@PathVariable String sessionId) {
        try {
            boolean exists = geminiAiService.sessionExists(sessionId);
            return RestResult.success(exists);
        } catch (Exception e) {
            log.error("检查会话存在失败: sessionId={}", sessionId, e);
            return RestResult.error("检查会话存在失败");
        }
    }

    /**
     * 刷新指定会话的TTL
     */
    @PostMapping("/sessions/refresh/{sessionId}")
    public RestResult<Void> refreshSessionTtl(@PathVariable String sessionId) {
        try {
            geminiAiService.refreshSessionTtl(sessionId);
            return RestResult.success(null);
        } catch (Exception e) {
            log.error("刷新会话TTL失败: sessionId={}", sessionId, e);
            return RestResult.error("刷新会话TTL失败");
        }
    }
}