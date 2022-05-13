package com.example.hemahotel;

import com.example.hemahotel.elasticSearch.SearchHotel;
import com.example.hemahotel.elasticSearch.SearchHotelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@SpringBootTest
class HeMaHotelApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private SearchHotelRepository searchHotelRepository;
//
//    //创建索引
//    @Test
//    void createIndex() {
//        //创建索引，系统初始化会自动创建索引
//        System.out.println("创建索引");
//    }
//
//    //删除索引
//    @Test
//    void deleteIndex() {
//        boolean delete = elasticsearchRestTemplate.indexOps(SearchHotel.class).delete();
//        System.out.println("删除索引：" + delete);
//    }
//
    //新增文档
    @Test
    void save() {
        SearchHotel  searchHotel= new SearchHotel();
        searchHotel.setId(1L);
        searchHotel.setName("北京丽景湾国际酒店");
        searchHotel.setLocation("北京东四环十里堡北里28号");
        searchHotel.setStar(5);
        searchHotel.setPicture("https://img14.360buyimg.com/hotel/jfs/t1/172321/37/10598/775776/60a629f4Ee9175b6b/00c800117e6a7381.png");
        SearchHotel save = searchHotelRepository.save(searchHotel);
    }

}
