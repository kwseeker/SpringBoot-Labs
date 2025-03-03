package top.kwseeker.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "articles")   //Article 对应一个文档，文档可以类比一个json, ES 一个索引现在只能有一个类型，可以有多个文档
                                    //articles 是文档所归属的索引名
@NoArgsConstructor
public class Article {

    // 文档的唯一ID
    @Id
    private String id;

    // 文档的字段, 类比 json 内的 KV, 一个文档可以有多个KV
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    //Keyword：不进行分词，整个字符串作为一个整体进行索引，适用于精确匹配查询，如 term 查询。
    //Text：进行分词，文本被拆分成多个词项进行索引，适用于全文搜索，如 match 查询。
    @Field(type = FieldType.Keyword)
    //private List<Tag> tags;   // 需要自定义转换器
    private List<String> tags;

    //点赞数
    @Field(type = FieldType.Integer)
    private Integer likeCount;

    // FieldType.Object 用于存储嵌套对象，但这些对象被视为单个文档的一部分，而不是独立的实体
    // FieldType.Nested 每个嵌套对象都被索引为一个独立的文档，这意味着可以对嵌套对象进行复杂的查询和过滤
    @Field(type = FieldType.Object)
    private User author;

    // 创建时间
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date createTime;

    // 修改时间
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date updateTime;

    public Article(String title, String content, List<String> tags, Integer likeCount, User author, Date createTime) {
        new Article(null, title, content, tags, likeCount, author, createTime);
    }

    public Article(String id, String title, String content, List<String> tags, Integer likeCount, User author, Date createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.likeCount = likeCount;
        this.author = author;
        this.createTime = createTime;
        this.updateTime = createTime;
    }
}
