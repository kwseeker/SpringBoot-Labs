package top.kwseeker.springboot.springdataelasticsearch.dataobject;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import top.kwseeker.springboot.springdataelasticsearch.constant.FieldAnalyzer;

//旧版本写法
//@Document(indexName = "product", // 索引名
//        type = "product", // 类型。未来的版本即将废弃
//        shards = 1, // 默认索引分区数
//        replicas = 0, // 每个分区的备份数
//        refreshInterval = "-1" // 刷新间隔
//)
//spring-data-elaticsearch 5.0.4写法
@Document(indexName = "product", createIndex = true)
@Setting(shards = 1, replicas = 0, refreshInterval = "-1")
public class ESProductDO {

    /**
     * ID 主键
     */
    @Id
    private Integer id;

    /**
     * SPU 名字
     */
    @Field(analyzer = FieldAnalyzer.IK_MAX_WORD, type = FieldType.Text)
    private String name;
    /**
     * 卖点
     */
    @Field(analyzer = FieldAnalyzer.IK_MAX_WORD, type = FieldType.Text)
    private String sellPoint;
    /**
     * 描述
     */
    @Field(analyzer = FieldAnalyzer.IK_MAX_WORD, type = FieldType.Text)
    private String description;
    /**
     * 分类编号
     */
    private Integer cid;
    /**
     * 分类名
     */
    @Field(analyzer = FieldAnalyzer.IK_MAX_WORD, type = FieldType.Text)
    private String categoryName;

    public Integer getId() {
        return id;
    }

    public ESProductDO setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ESProductDO setName(String name) {
        this.name = name;
        return this;
    }

    public String getSellPoint() {
        return sellPoint;
    }

    public ESProductDO setSellPoint(String sellPoint) {
        this.sellPoint = sellPoint;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ESProductDO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Integer getCid() {
        return cid;
    }

    public ESProductDO setCid(Integer cid) {
        this.cid = cid;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ESProductDO setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    @Override
    public String toString() {
        return "ProductDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sellPoint='" + sellPoint + '\'' +
                ", description='" + description + '\'' +
                ", cid=" + cid +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }

}
