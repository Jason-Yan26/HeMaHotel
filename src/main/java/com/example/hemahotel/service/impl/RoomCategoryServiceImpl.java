package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.RoomCategoryRepository;
import com.example.hemahotel.entity.Hotel;
import com.example.hemahotel.entity.RoomCategory;
import com.example.hemahotel.service.RoomCategoryService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomCategoryServiceImpl implements RoomCategoryService {

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

    @Override
    public ResponseUtils getRoomInformationByHotelId(Long hotelId) {

        JSONObject jsonObject = new JSONObject();
        List<RoomCategory> roomInformation = roomCategoryRepository.findByHotelId(hotelId);

        if(!roomInformation.isEmpty()){
            return ResponseUtils.success("房间信息查找成功",  roomInformation);
        }
        else {
            jsonObject.put("hotelId", hotelId);
            return ResponseUtils.response(400,"房间信息为空", jsonObject);
        }
    }

    @Override
    public ResponseUtils getRoomInformationByHotelIdAndRoomCategoryId(Long hotelId, Long RoomCategoryId) {

        JSONObject jsonObject = new JSONObject();
        Optional<RoomCategory> r = roomCategoryRepository.findByIdAndHotelId(RoomCategoryId,hotelId);

        if(!r.isPresent()){
            return ResponseUtils.response(400, "该酒店/房间类型不存在，请重新查找", jsonObject);
        }
        else {
            RoomCategory room = r.get();
            return ResponseUtils.response(200, "查找成功", room);
        }
    }


}
