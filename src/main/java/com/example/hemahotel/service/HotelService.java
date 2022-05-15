package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

public interface HotelService {

    public ResponseUtils CreateComment(Long userId, String comment, Long hotelId, int star);

    public ResponseUtils findCommentByHotelId(Long productId);


    /** 酒店名称自动补全*/
    public ResponseUtils hotelNameCompletion(String prefix);

    /** 酒店关键字搜索*/
    public ResponseUtils searchHotelByKeyword(String searchKeyWord,int page,int pageNum,int lowerStar,int upperStar);

}
