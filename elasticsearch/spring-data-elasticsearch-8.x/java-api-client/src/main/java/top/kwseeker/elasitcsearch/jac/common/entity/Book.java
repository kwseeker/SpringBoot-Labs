package top.kwseeker.elasitcsearch.jac.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Book {

    private String id;
    private String title;
    private String language;
    private String author;
    private Float price;
    @JsonProperty("publish_time")
    private String publishTime;
    private String description;
}