package top.kwseeker.elasticsearch.rhlc.executor;

import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import top.kwseeker.elasticsearch.rhlc.common.entity.Book;
import top.kwseeker.elasticsearch.rhlc.common.enums.AnalyzerEnum;
import top.kwseeker.elasticsearch.rhlc.common.enums.BookFieldEnum;
import top.kwseeker.elasticsearch.rhlc.common.query.BasicQuery;
import top.kwseeker.elasticsearch.rhlc.common.response.PageResponse;

public class EsQueryExecutorTest {

    private EsQueryExecutor esQueryExecutor;

    @BeforeClass
    public void init() {
        String hosts = "127.0.0.1:9201";
        esQueryExecutor = new EsQueryExecutor(hosts);
    }

    @Test
    public void testSearch() {
        String indexName = "idx_books";
        int pageNum = 1;
        int pageSize = 10;
        BasicQuery query = new BasicQuery();
        query.setText("Java编程");
        Class<Book> clazz = Book.class;

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //{
        //  "query": {
        //    "multi_match": {
        //      "query": "Java编程",
        //      "analyzer": "ik_smart",
        //      "fields": ["title", "description"]
        //    }
        //  }
        //}
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery(query.getText(),
                    BookFieldEnum.TITLE.getFieldName(),
                    BookFieldEnum.DESCRIPTION.getFieldName())
                .analyzer(AnalyzerEnum.IK_SMART.getAnalyzerName());

        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchSourceBuilder.size(pageSize).from(pageSize * (pageNum - 1));
        PageResponse<Book> pageResponse = esQueryExecutor.search(indexName, searchSourceBuilder, clazz, pageNum, pageSize);

        Assert.assertEquals(pageResponse.getTotal(), 2);
    }
}