package top.kwseeker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.kwseeker.po.Joke;

import java.util.List;

public interface JokeRepository extends ElasticsearchRepository<Joke, String> {

    List<Joke> findByTitle(String title);

    @Query("{\"match\": {\"title\": {\"query\": \"?0\"}}}")
    List<Joke> findByTitleCustom(String title);

    // 分页查询
    Page<Joke> findByTitle(String title, Pageable pageable);

    List<Joke> findByContent(String content);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"content\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}}]}}")
    List<Joke> findByContentFuzzy(String content);

    List<Joke> findByTitleAndContent(String title, String content);

    List<Joke> findByTitleOrContent(String title, String content);

    // 等值查询
    List<Joke> findByLikeCount(Integer likeCount);
    // 范围查询
    List<Joke> findByLikeCountBetween(Integer min, Integer max);
    List<Joke> findByLikeCountGreaterThan(Integer likeCount);
    List<Joke> findByLikeCountGreaterThanEqual(Integer likeCount);
    List<Joke> findByLikeCountLessThan(Integer likeCount);

}

//#条件查询, 如要查询age等于28岁的 _search?q=*:***
//4 GET /es_db/_doc/_search?q=age:28
//5 6
//#范围查询, 如要查询age在25至26岁之间的 _search?q=***[** TO **] 注意: TO 必
//须为大写
//7 GET /es_db/_doc/_search?q=age[25 TO 26]
//8 9
//#查询年龄小于等于28岁的 :<=
//10 GET /es_db/_doc/_search?q=age:<=28
//11 #查询年龄大于28前的 :>
//12 GET /es_db/_doc/_search?q=age:>28
//13
//14 #分页查询 from=*&size=*
//15 GET /es_db/_doc/_search?q=age[25 TO 26]&from=0&size=1
//16
//17 #对查询结果只输出某些字段 _source=字段,字段
//18 GET /es_db/_doc/_search?_source=name,age
//19
//20 #对查询结果排序 sort=字段:desc/asc
//21 GET /es_db/_doc/_search?sort=age:desc

//如何实现嵌套文档的存储索引查询
//public class Address {
//    String city, street;
//    Point location;
//}

//如何使用 DSL 查询 （@Query 自定义查询）

