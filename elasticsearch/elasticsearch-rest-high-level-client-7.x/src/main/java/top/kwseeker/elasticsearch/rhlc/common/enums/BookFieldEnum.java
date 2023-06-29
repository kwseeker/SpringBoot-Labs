package top.kwseeker.elasticsearch.rhlc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookFieldEnum {

    ID("id"),
    TITLE("title"),
    LANGUAGE("language"),
    AUTHOR("author"),
    PRICE("price"),
    PUBLISH_TIME("publish_time"),
    DESCRIPTION("description");

    private final String fieldName;
}
