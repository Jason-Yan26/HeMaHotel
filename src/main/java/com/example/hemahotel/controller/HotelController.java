package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.RoomRepository;
import com.example.hemahotel.entity.RoomCategory;
import com.example.hemahotel.service.HotelService;
import com.example.hemahotel.service.RoomCategoryService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

      @Autowired
      private HotelService hotelService;

      @Autowired
      private RoomCategoryService roomCategoryService;

      //通过产品id获得评论接口
      @PostMapping("/comment")
      public ResponseUtils GetComments(@RequestBody String jsonStr){

          JSONObject jsonObject = JSON.parseObject(jsonStr);

          return hotelService.findCommentByHotelId(jsonObject.getLong("hotelId"));
      }

    @PostMapping("/room/information/all")
    public ResponseUtils GetRoomInformationAll(@RequestBody JSONObject jsonObject){

        Long hotelId=jsonObject.getLong("hotelId");

        return roomCategoryService.GetRoomInformationByHotelId(hotelId);
    }
    @PostMapping("/room/information/one")
    public ResponseUtils GetRoomInformationOne(@RequestBody JSONObject jsonObject){

        Long hotelId=jsonObject.getLong("hotelId");
        Long roomCategoryId=jsonObject.getLong("roomCategoryId");

        return roomCategoryService.GetRoomInformationByHotelIdAndRoomCategoryId(hotelId,roomCategoryId);
    }
}
