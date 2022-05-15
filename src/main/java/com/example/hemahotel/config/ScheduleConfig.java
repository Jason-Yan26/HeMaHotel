package com.example.hemahotel.config;

import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.elasticSearch.SearchHotel;
import com.example.hemahotel.elasticSearch.SearchHotelRepository;
import com.example.hemahotel.entity.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class ScheduleConfig {
    @Autowired
    SearchHotelRepository  searchHotelRepository;

    @Autowired
    HotelRepository hotelRepository;

    //每两个小时更新一次
    @Scheduled(cron = "0 0 0/12 * * *")
    public void ElasticSearchSchedule(){
        searchHotelRepository.deleteAll();
        searchHotelRepository.saveAll(ConvertHotel(hotelRepository.findAll()));
    }

    //将酒店类转换成搜索酒店类（放在ES中）
    public static List<SearchHotel> ConvertHotel(List<Hotel> hotels){
        List<SearchHotel> searchHotels = new ArrayList<>();
//        for(Hotel h : hotels){
//            searchHotels.add(SearchHotel.builder()
//                    .id(h.getId())
//                    .name(h.getName())
//                    .location(h.getLocation())
//                    .star(h.getStar())
//                    .build());
//        }
        return searchHotels;
    }
}
