package top.kwseeker.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.kwseeker.po.Article;
import top.kwseeker.po.Author;
import top.kwseeker.po.User;

import java.util.List;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    // 借助标签tags和作者author查询文章
    //@Query("""
    //        {
    //          "bool": {
    //            "must": [
    //              { "terms": { "tags": "?0" } },
    //              {
    //                "nested": {
    //                  "path": "author",
    //                  "query": {
    //                    "bool": { "must": [{ "term": { "author.id": "?1.id" } }] }    // 占位符取字段值，AI瞎写的，根本没有这种用法
    //                  }
    //                }
    //              }
    //            ]
    //          }
    //        }""")
    // TODO SpEL 取对象的值 #{#author.id} 是 Spring Data ElasticSearch 官网给的例子，但是测试却查不到数据？？？
    @Query("""
            {
              "bool": {
                "must": [
                  { "terms": { "tags": ?0 } },
                  { "term": { "author.id": "#{#author.id}" } }
                ]
              }
            }""")
    List<Article> findByTagsAndAuthor(List<String> tags, Author author);

    @Query("""
            {
              "bool": {
                "must": [
                  { "term": { "author.id": "#{#author.id}" } }
                ]
              }
            }""")
    List<Article> findByAuthor(Author author);

    @Query("""
            {
              "bool": {
                "must": [
                  { "terms": { "tags": ?0 } },
                  { "term": { "author.id": "?1" } }
                ]
              }
            }""")
    List<Article> findByTagsAndAuthorId(List<String> tags, String authorId);

    // 借助标签tags和作者author查询文章和文件内容查询文章
    //List<Article> findByTagsAndAuthorAndContent(List<String> tags, User author, String content);
}
