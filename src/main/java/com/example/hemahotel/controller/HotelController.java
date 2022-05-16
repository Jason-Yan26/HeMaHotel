package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.RoomRepository;
import com.example.hemahotel.entity.Reservation;
import com.example.hemahotel.entity.RoomCategory;
import com.example.hemahotel.service.HotelService;
import com.example.hemahotel.service.RoomCategoryService;
import com.example.hemahotel.service.RoomService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
@CrossOrigin(origins = "*")
public class HotelController {

      @Autowired
      private HotelService hotelService;

      @Autowired
      private RoomCategoryService roomCategoryService;

      @Autowired
      private RoomService roomService;

      //通过hotelId获得评论接口
      @PostMapping("/comment")
      public ResponseUtils GetComments(@RequestBody JSONObject jsonObject){

          Long hotelId = jsonObject.getLong("hotelId");

          return hotelService.findCommentByHotelId(hotelId);
      }

    @PostMapping("/room/information/all")
    public ResponseUtils GetRoomInformationAll(@RequestBody JSONObject jsonObject){

        Long hotelId=jsonObject.getLong("hotelId");

        return roomCategoryService.GetRoomInformationByHotelId(hotelId);
    }

    /** 获取某一房型当前的空闲房间数 */
    @PostMapping("/category/freeNum")
    public ResponseUtils GetFreeNumByRoomCategoryId(@RequestBody JSONObject jsonObject){

        Long roomCategoryId=jsonObject.getLong("roomCategoryId");

        return roomService.getFreeNumByRoomCategoryId(roomCategoryId);
    }

    @PostMapping("/room/information/one")
    public ResponseUtils GetRoomInformationOne(@RequestBody JSONObject jsonObject){

        Long hotelId=jsonObject.getLong("hotelId");
        Long roomCategoryId=jsonObject.getLong("roomCategoryId");

        return roomCategoryService.GetRoomInformationByHotelIdAndRoomCategoryId(hotelId,roomCategoryId);
    }

    /** 酒店名称自动补全接口*/
    @PostMapping("/name/completion")
    public ResponseUtils getNameCompletion(@RequestBody JSONObject jsonObject){

        String prefix = jsonObject.getString("hotel_name_prefix");
        return hotelService.hotelNameCompletion(prefix);
    }

    /** 酒店关键字搜索接口*/
    @PostMapping("/search")
    public ResponseUtils searchHotelByKeyword(@RequestBody JSONObject jsonObject){

        String searchKeyWord = jsonObject.getString("searchKeyWord");//搜索关键字
        int page = jsonObject.getInteger("page");//页数
        int pageNum = jsonObject.getInteger("pageNum");//每页酒店信息的个数
        int lowerStar = jsonObject.getInteger("lowerStar");//酒店星级下界
        int upperStar = jsonObject.getInteger("upperStar");//酒店星级上界

        return hotelService.searchHotelByKeyword(searchKeyWord,page,pageNum,lowerStar,upperStar);
    }

    /** 酒店关信息获取接口*/
    @PostMapping("/information")
    public ResponseUtils getHotelInformation(@RequestBody JSONObject jsonObject){

        Long hotelId = jsonObject.getLong("hotelId");

        return hotelService.getHotelInformation(hotelId);
    }

    /** 酒店推荐接口*/
    @PostMapping("/recommend")
    public ResponseUtils getHotelRecommendation(@RequestBody JSONObject jsonObject){

        Long num = jsonObject.getLong("num");

        return hotelService.getHotelRecommendation(num);
    }

}
