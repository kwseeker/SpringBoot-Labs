package top.kwseeker.elasticsearch.rhlc.common.entity;

import lombok.Data;

@Data
public class Book {

    private String id;
    private String title;
    private String language;
    private String author;
    private Float price;
    private String publishTime;
    private String description;
}
