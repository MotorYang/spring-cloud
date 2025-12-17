package com.yangxy.cloud.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Article DTO (Data Transfer Object)
 * 用于 API 请求和响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {

    private String id;

    private String title;

    private String excerpt;

    private String content;

    private String author;

    private LocalDate date;

    private String category;

    private String imageUrl;

    private List<String> tags;

    private Integer views;
}