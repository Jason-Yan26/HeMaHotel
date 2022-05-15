package com.example.hemahotel;

import com.example.hemahotel.config.ScheduleConfig;
import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.elasticSearch.SearchHotel;
import com.example.hemahotel.elasticSearch.SearchHotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import javax.annotation.PostConstruct;

@SpringBootApplication

public class HeMaHotelApplication {

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Autowired
    SearchHotelRepository searchHotelRepository;

    @Autowired
    HotelRepository hotelRepository;

    public static void main(String[] args) {

        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(HeMaHotelApplication.class, args);
    }


//    //启动时将mysql数据库hotel表中的信息导入elasticsearch中的hotel这个index中
//    @PostConstruct
//    public void buildIndex() {
////        elasticsearchOperations.indexOps(SearchHotel.class).refresh();
//        searchHotelRepository.deleteAll();
//        searchHotelRepository.saveAll(ScheduleConfig.ConvertHotel(hotelRepository.findAll()));
//    }

}
