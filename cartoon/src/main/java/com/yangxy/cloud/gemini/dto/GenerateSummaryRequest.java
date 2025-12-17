package com.yangxy.cloud.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 生成摘要请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateSummaryRequest {

    /**
     * 博客内容
     */
    private String content;

    /**
     * 语言：en 或 zh
     */
    private String lang = "zh";
}