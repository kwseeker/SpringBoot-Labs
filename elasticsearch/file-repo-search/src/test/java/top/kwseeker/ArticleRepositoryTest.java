package top.kwseeker;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import top.kwseeker.po.Article;
import top.kwseeker.po.Author;
import top.kwseeker.po.Tag;
import top.kwseeker.po.User;
import top.kwseeker.repository.ArticleRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ArticleRepositoryTest {

    @Resource
    private ArticleRepository articleRepository;

    @Test
    @Order(1)
    public void testSaveArticle() {
        articleRepository.deleteAll();

        List<Article> articles = new ArrayList<>();
        articles.add(new Article("1",
                "ElasticSearch源码分析",
                "ElasticSearch 架构分为以下几个部分...",
                Arrays.asList(Tag.JAVA.getCode(), Tag.ES.getCode()),
                666, new User("101", "Arvin"),
                new Date()));
        articles.add(new Article("2",
                "论腰间盘突出的缓解方法",
                "导致腰间盘突出的原因...，平时应该注意...",
                List.of(Tag.LIFE.getCode()),
                233, new User("101", "Arvin"),
                new Date()));
        articles.add(new Article("3",
                "Spring Data ElasticSearch 类型转换器",
                "Spring Data ElasticSearch 类型转换器 ...",
                List.of(Tag.ES.getCode()),
                666, new User("101", "Arvin"),
                new Date()));
        articles.add(new Article("4",
                "ElasticSearch DSL",
                "ElasticSearch DSL 是...，提供下面功能...",
                List.of(Tag.ES.getCode()),
                666, new User("102", "Bob"),
                new Date()));
        articleRepository.saveAll(articles);
    }

    @Test
    @Order(99)
    public void testDeleteArticle() {
        articleRepository.deleteAll();
    }

    @Test
    @Order(2)
    public void testFindArticles() {
        List<Article> articles = articleRepository.findByTagsAndAuthorId(
                List.of(Tag.ES.getCode()), "101");
        assertEquals(2, articles.size());

        // TODO 下面这两种写法查不到数据，不知道为何，有空跟下源码
        //articles = articleRepository.findByAuthor(new Author("101", "Arvin"));
        //articles = articleRepository.findByTagsAndAuthor(
        //        List.of(Tag.ES.getCode()), new Author("101", "Arvin"));
        //assertEquals(2, articles.size());
    }
}
