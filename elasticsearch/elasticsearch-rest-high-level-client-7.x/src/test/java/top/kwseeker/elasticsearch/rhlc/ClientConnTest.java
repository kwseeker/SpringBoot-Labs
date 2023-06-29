package top.kwseeker.elasticsearch.rhlc;

import org.apache.http.HttpHost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.testng.annotations.Test;

public class ClientConnTest {

    public static final String IndexName = "idx_accounts";

    /**
     * GET idx_accounts/_search
     * {
     * "query": {
     * "match_all": {}
     * }
     * }
     */
    @Test
    public void testClientInitialization() {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9201, "http")))) {

            SearchRequest request = new SearchRequest(IndexName);
            SearchSourceBuilder ssb = new SearchSourceBuilder()
                    .query(QueryBuilders.matchAllQuery());
            request.source(ssb);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            RestStatus status = response.status();
            TimeValue took = response.getTook();
            SearchHits hits = response.getHits();
            TotalHits totalHits = hits.getTotalHits();
            SearchHit[] searchHits = hits.getHits();
            int count = 0;
            for (SearchHit hit : searchHits) {
                if (count++ < 5) {
                    System.out.println(hit.getSourceAsString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}