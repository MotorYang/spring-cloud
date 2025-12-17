package com.yangxy.cloud.article.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangxy.cloud.article.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Article Mapper (MyBatis-Plus)
 * 数据访问层接口
 */
@Mapper
public interface ArticleDao extends BaseMapper<Article> {

    /**
     * 增加文章浏览量
     * @param id 文章ID
     * @return 受影响的行数
     */
    @Update("UPDATE articles SET views = views + 1 WHERE id = #{id}")
    int incrementViews(@Param("id") String id);
}