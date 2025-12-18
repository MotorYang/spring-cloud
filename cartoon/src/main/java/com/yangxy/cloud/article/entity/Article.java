package com.yangxy.cloud.article.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yangxy.cloud.config.PostgreSQLJsonbTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "articles", autoResultMap = true)
public class Article {

    /**
     * 文章ID，使用 UUID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 文章摘要
     */
    @TableField("excerpt")
    private String excerpt;

    /**
     * 文章内容
     */
    @TableField("content")
    private String content;

    /**
     * 作者
     */
    @TableField("author")
    private String author;

    /**
     * 发布日期
     */
    @TableField("date")
    private OffsetDateTime date;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 标签列表，使用 JSON 类型存储
     */
    @TableField(value = "tags", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private List<String> tags;

    /**
     * 浏览量
     */
    @TableField("views")
    private Integer views;
}
