package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

public interface HotelService {

    public ResponseUtils CreateComment(Long userId, String comment, Long hotelId, int star);

    public ResponseUtils findCommentByHotelId(Long productId);
}
