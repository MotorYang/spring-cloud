package com.yangxy.cloud.article.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Article 创建 DTO
 * 用于创建文章的请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreateDTO {

    private String title;

    private String excerpt;

    private String content;

    private String author;

    private OffsetDateTime date;

    private String category;

    private String imageUrl;

    private List<String> tags;
}