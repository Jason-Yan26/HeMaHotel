package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.service.HotelService;
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

      //通过产品id获得评论接口
      @PostMapping("/comment")
      public ResponseUtils GetComments(@RequestBody String jsonStr){

          JSONObject jsonObject = JSON.parseObject(jsonStr);

          return hotelService.findCommentByHotelId(jsonObject.getLong("hotelId"));
      }

}
