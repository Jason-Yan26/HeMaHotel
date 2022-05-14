package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

import java.util.List;

public interface RoomCategoryService {

    public ResponseUtils GetRoomInformationByHotelId(Long hotelId);

    public ResponseUtils GetRoomInformationByHotelIdAndRoomCategoryId(Long hotelId,Long RoomCategoryId);


}
