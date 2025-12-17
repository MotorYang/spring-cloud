package com.yangxy.cloud.article.controller;

import com.yangxy.cloud.article.dto.ArticleCreateDTO;
import com.yangxy.cloud.article.dto.ArticleDTO;
import com.yangxy.cloud.article.service.ArticleServiceOptimized;
import com.yangxy.cloud.common.response.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Article REST API 控制器
 */
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleServiceOptimized articleService;

    /**
     * 获取所有文章
     * GET /api/articles
     */
    @GetMapping("/all")
    public RestResult<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleService.getAllArticles();
        return RestResult.success(articles);
    }

    /**
     * 根据ID获取文章
     * GET /api/articles/{id}
     */
    @GetMapping("/get/{id}")
    public RestResult<ArticleDTO> getArticleById(@PathVariable String id) {
        return articleService.getArticleById(id)
                .map(RestResult::success)
                .orElse(RestResult.build(11002, "文章不存在！", null));
    }

    /**
     * 创建新文章
     * POST /api/articles
     */
    @PostMapping("/create")
    public RestResult<ArticleDTO> createArticle(@RequestBody ArticleCreateDTO createDTO) {
        ArticleDTO createdArticle = articleService.createArticle(createDTO);
        return RestResult.success(createdArticle);
    }

    /**
     * 删除文章
     * DELETE /api/articles/{id}
     */
    @DeleteMapping("/del/{id}")
    public RestResult<Void> deleteArticle(@PathVariable String id) {
        boolean deleted = articleService.deleteArticle(id);
        if (deleted) {
            return RestResult.success(null);
        }
        return RestResult.build(11002, "文章不存在!", null);
    }

    /**
     * 增加文章浏览量
     * POST /api/articles/{id}/increment-views
     */
    @PostMapping("/views/{id}")
    public RestResult<Integer> incrementViews(@PathVariable String id) {
        Integer views = articleService.incrementViews(id);
        return RestResult.success(views);
    }

    /**
     * 按分类获取文章
     * GET /api/articles/category/{category}
     */
    @GetMapping("/category/{category}")
    public RestResult<List<ArticleDTO>> getArticlesByCategory(@PathVariable String category) {
        List<ArticleDTO> articles = articleService.getArticlesByCategory(category);
        return RestResult.success(articles);
    }

    /**
     * 按作者获取文章
     * GET /api/articles/author/{author}
     */
    @GetMapping("/author/{author}")
    public RestResult<List<ArticleDTO>> getArticlesByAuthor(@PathVariable String author) {
        List<ArticleDTO> articles = articleService.getArticlesByAuthor(author);
        return RestResult.success(articles);
    }

    /**
     * 搜索文章
     * GET /api/articles/search?keyword={keyword}
     */
    @GetMapping("/search")
    public RestResult<List<ArticleDTO>> searchArticles(@RequestParam String keyword) {
        List<ArticleDTO> articles = articleService.searchArticles(keyword);
        return RestResult.success(articles);
    }
}