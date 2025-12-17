package com.yangxy.cloud.gemini.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 生成博客内容请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateBlogRequest {

    /**
     * 博客标题
     */
    private String title;

    /**
     * 额外的上下文或指令（可选）
     */
    private String context;

    /**
     * 语言：en 或 zh
     */
    private String lang = "zh";
}