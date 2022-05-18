package com.example.hemahotel.config;

import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.elasticSearch.SearchHotel;
import com.example.hemahotel.elasticSearch.SearchHotelRepository;
import com.example.hemahotel.entity.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.suggest.Completion;
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

    //每天凌晨3点更新
//    @Scheduled(cron = "0 0 3 * * ?")
//    public void ElasticSearchSchedule(){
//        searchHotelRepository.deleteAll();
//        searchHotelRepository.saveAll(ConvertHotel(hotelRepository.findAll()));
//    }

    //将酒店类转换成搜索酒店类
    public static List<SearchHotel> ConvertHotel(List<Hotel> hotels){
        List<SearchHotel> searchHotels = new ArrayList<>();
        for(Hotel h : hotels){

            List<String> suggestList = new ArrayList<>();
            suggestList.add(h.getName()); //可以把多个内容作为suggest的数据源
//            suggestList.add(h.getLocation());
            Completion suggest = new Completion(suggestList.toArray(new String[suggestList.size()]));

            searchHotels.add(SearchHotel.builder()
                    .id(h.getId())
                    .name(h.getName())
//                    .picture(h.getPicture())
                    .location(h.getLocation())
                    .star(h.getStar())
                    .suggestion(suggest)
                    .build());
        }
        return searchHotels;
    }
}
