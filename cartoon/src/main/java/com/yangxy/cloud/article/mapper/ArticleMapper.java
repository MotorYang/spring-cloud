package com.yangxy.cloud.article.mapper;
import com.yangxy.cloud.article.dto.ArticleCreateDTO;
import com.yangxy.cloud.article.dto.ArticleDTO;
import com.yangxy.cloud.article.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Article MapStruct Mapper
 * 用于实体类和 DTO 之间的转换
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArticleMapper {

    /**
     * Entity 转 DTO
     */
    ArticleDTO toDTO(Article article);

    /**
     * DTO 转 Entity
     */
    Article toEntity(ArticleDTO articleDTO);

    /**
     * CreateDTO 转 Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "views", constant = "0")
    Article toEntity(ArticleCreateDTO createDTO);

    Article updateEntity(ArticleCreateDTO updateDTO);

    /**
     * Entity List 转 DTO List
     */
    List<ArticleDTO> toDTOList(List<Article> articles);
}
