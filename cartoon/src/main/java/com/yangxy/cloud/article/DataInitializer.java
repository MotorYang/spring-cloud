package com.yangxy.cloud.article;

import com.yangxy.cloud.article.entity.Article;
import com.yangxy.cloud.article.mapper.ArticleDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库初始化类
 * 在应用启动时插入初始文章数据
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ArticleDao articleDao;

    @Override
    public void run(String... args) {
        // 检查数据库是否已有数据
        Long count = articleDao.selectCount(null);
        if (count > 0) {
            System.out.println("数据库已有数据，跳过初始化");
            return;
        }

        // 创建初始文章数据
        List<Article> initialArticles = Arrays.asList(
                createArticle(
                        "1",
                        "Why I Love Coding in Pajamas",
                        "The ultimate guide to comfort-driven development and why strict dress codes are overrated.",
                        """
                        Let's face it: productivity peaks when you are comfortable.
        
                        ## The Elastic Waistband Advantage
        
                        There is a direct correlation between the stretchiness of your waistband and the quality of your code. Studies (that I made up) show that developers in pajamas are 50% less likely to create bugs because they are too relaxed to stress about edge cases.
        
                        ## Coffee Stains are Badges of Honor
        
                        When you work from home in your favorite PJs, a coffee stain isn't a disaster; it's a timestamp of your morning stand-up.
        
                        In conclusion, maximize your comfort to maximize your output.
                        """,
                        "DevDood",
                        LocalDate.of(2023, 10, 24),
                        "Lifestyle",
                        "https://picsum.photos/800/400?random=1",
                        Arrays.asList("Coding", "Humor", "WFH"),
                        1205
                ),

                createArticle(
                        "2",
                        "React vs. The World",
                        "A cartoonish battle between web frameworks. Who will win the DOMination?",
                        """
                        Imagine a wrestling ring. In one corner, wearing blue trunks, the Component Crusher: React!
        
                        In the other corner, the Green Giant: Vue!
        
                        ## Round 1: Boilerplate
        
                        React throws a hook! It's super effective. Vue counters with a v-model directive, simplifying two-way binding. The crowd goes wild.
        
                        ## Round 2: Ecosystem
        
                        React summons its massive ecosystem army. Libraries rain down from the sky. It's chaos. But wait, is that Svelte coming in with a steel chair?
        
                        Ultimately, we're all winners because we get to build cool stuff.
                        """,
                        "TechToon",
                        LocalDate.of(2023, 10, 25),
                        "Tech",
                        "https://picsum.photos/800/400?random=2",
                        Arrays.asList("React", "Vue", "Frontend"),
                        892
                ),

                createArticle(
                        "3",
                        "Top 5 Snacks for Late Night Debugging",
                        "Fuel your brain without getting cheese dust on your keyboard (optional).",
                        """
                        1. **Pretzels**: Low mess, high crunch. Satisfying aggression release when the API fails.
                        2. **Gummy Bears**: Quick sugar rush for that recursive function logic.
                        3. **Dark Chocolate**: Sophisticated, just like your refactored code.
                        4. **Popcorn**: Risky, but worth it. Use chopsticks to keep fingers clean.
                        5. **Water**: Boring, but necessary. Stay hydrated, folks.
                        """,
                        "SnackMaster",
                        LocalDate.of(2023, 10, 26),
                        "Food",
                        "https://picsum.photos/800/400?random=3",
                        Arrays.asList("Food", "Health", "Tips"),
                        430
                )
        );

        // 批量插入数据
        initialArticles.forEach(articleDao::insert);
        System.out.println("初始文章数据加载成功！共 " + initialArticles.size() + " 篇文章");
    }

    private Article createArticle(String id, String title, String excerpt, String content,
                                  String author, LocalDate date, String category,
                                  String imageUrl, List<String> tags, Integer views) {
        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setExcerpt(excerpt);
        article.setContent(content);
        article.setAuthor(author);
        article.setDate(date);
        article.setCategory(category);
        article.setImageUrl(imageUrl);
        article.setTags(tags);
        article.setViews(views);
        return article;
    }
}