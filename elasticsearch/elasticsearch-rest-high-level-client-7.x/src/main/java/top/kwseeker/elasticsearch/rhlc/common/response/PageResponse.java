package top.kwseeker.elasticsearch.rhlc.common.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

    private List<T> data;

    private int pageNum;

    private int pageSize;

    private long total;
}
