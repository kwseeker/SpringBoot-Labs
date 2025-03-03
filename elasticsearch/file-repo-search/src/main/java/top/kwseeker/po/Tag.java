package top.kwseeker.po;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tag {
    JAVA("JAVA", "Java","JAVA技术相关"),
    ES("ES", "ElasticSearch", "ElasticSearch相关"),
    LIFE("LIFE", "生活", "生活相关");

    private final String code;
    private final String name;
    private final String desc;
}
