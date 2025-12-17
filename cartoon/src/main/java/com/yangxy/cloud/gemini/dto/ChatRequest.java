package com.yangxy.cloud.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 聊天请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    /**
     * 用户消息
     */
    private String message;

    /**
     * 会话ID（可选，用于保持上下文）
     */
    private String sessionId;
}