package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.FavoriteRepository;
import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.entity.Favorite;
import com.example.hemahotel.entity.Hotel;
import com.example.hemahotel.service.FavoriteService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService{

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public ResponseUtils add(Long userId, Long hotelId) {
        JSONObject jsonObject = new JSONObject();
        Optional<Hotel> h = hotelRepository.findById(hotelId);
        //如果商品不存在
        if(!h.isPresent()){
            return ResponseUtils.response(400, "该酒店不存在，收藏失败", jsonObject);
        }
        else{
            //该用户已收藏该商品
            if(favoriteRepository.findByUserIdAndHotelId(userId,hotelId).isPresent()){
                return ResponseUtils.response(401, "你已收藏该酒店，收藏失败", jsonObject);
            }
            else {
                Hotel hotel = h.get();
                Timestamp creatTime = new Timestamp(System.currentTimeMillis());
                favoriteRepository.save(new Favorite(userId, hotel.getId(), creatTime));
                return ResponseUtils.response(200, "收藏夹添加酒店成功", jsonObject);
            }
        }
    }

    @Override
    public ResponseUtils remove(Long userId, Long hotelId) {

        JSONObject jsonObject = new JSONObject();

        Optional<Favorite> favorite = favoriteRepository.findByUserIdAndHotelId(userId,hotelId);

        //该用户收藏夹中不存在该酒店
        if(!favorite.isPresent()){
            return ResponseUtils.response(400, "收藏夹移除酒店失败", jsonObject);
        }
        else{
            favoriteRepository.deleteById(favorite.get().getId());
            return ResponseUtils.response(200, "收藏夹移除酒店成功", jsonObject);
        }
    }

    @Override
    public ResponseUtils get(Long userId) {
        JSONObject jsonObject = new JSONObject();

        List<Favorite> favorites = favoriteRepository.findAllByUserIdOrderByCreateTimeDesc(userId);
        List<Map<String,Object>> res = new ArrayList<>();

        for(Favorite favorite:favorites){
            Map<String,Object> map= new HashMap<>();
            Long hId=favorite.getHotelId();
            Optional<Hotel> h = hotelRepository.findById(hId);
            if(!h.isPresent()) {
                return ResponseUtils.response(400, "收藏夹里该酒店"+ hId +"不存在，出现错误", jsonObject);
            }
            else {
                map.put("hotelId", hId);
                Hotel hotel = h.get();
                map.put("hotelName", hotel.getName());
                map.put("hotelPicture", hotel.getPicture());
                //map.put("hotelPrice", hotel.getPrice());
                res.add(map);
            }
        }
        jsonObject.put("hotels",res);
        return ResponseUtils.response(200, "收藏夹获取成功", jsonObject);
    }
}
