package com.example.hemahotel.elasticSearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchHotelRepository extends ElasticsearchRepository<SearchHotel,Long> {
}
