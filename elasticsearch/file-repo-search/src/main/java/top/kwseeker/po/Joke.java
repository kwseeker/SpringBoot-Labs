package top.kwseeker.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "jokes")   //Joke 对应一个文档，文档可以类比一个json, ES 一个索引现在只能有一个类型，可以有多个文档
                                    //jokes 是文档所归属的索引名
@NoArgsConstructor
public class Joke {

    // 文档的唯一ID
    @Id
    private String id;

    // 文档的字段, 类比 json 内的 KV, 一个文档可以有多个KV
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    //点赞数
    @Field(type = FieldType.Integer)
    private Integer likeCount;

    public Joke(String title, String content, Integer likeCount) {
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
    }
}
