package top.kwseeker.springboot.springdataelasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.kwseeker.springboot.springdataelasticsearch.dataobject.ESProductDO;

public interface ProductRepository extends ElasticsearchRepository<ESProductDO, Integer> {

}
