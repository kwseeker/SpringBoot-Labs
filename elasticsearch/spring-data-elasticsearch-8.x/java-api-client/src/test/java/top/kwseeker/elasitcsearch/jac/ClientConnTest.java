package top.kwseeker.elasitcsearch.jac;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.testng.annotations.Test;
import top.kwseeker.elasitcsearch.jac.common.entity.Book;

import java.io.IOException;

public class ClientConnTest {

    @Test
    public void testConnection() throws IOException {
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9201)).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        //{
        //  "query": {
        //    "multi_match": {
        //      "query": "Java编程",
        //      "analyzer": "ik_smart",
        //      "fields": ["title", "description"]
        //    }
        //  }
        //}
        SearchResponse<Book> searchResponse = client.search(s -> s
                        .index("idx_books")
                        .query(q -> q
                                .multiMatch(t -> t
                                        .analyzer("ik_smart")
                                        .fields("title", "description")
                                        .query("Java编程")
                                )
                        ),
                Book.class);

        for (Hit<Book> hit : searchResponse.hits().hits()) {
            processProduct(hit.source());
        }
    }

    private void processProduct(Book book) {
        try {
            //使用jackson将book转成String
            String bookStr = new ObjectMapper().writeValueAsString(book);
            System.out.println(bookStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
