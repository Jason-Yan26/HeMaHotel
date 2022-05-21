package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

public interface RoomCategoryService {

    public ResponseUtils getRoomInformationByHotelId(Long hotelId);

    public ResponseUtils getRoomInformationByHotelIdAndRoomCategoryId(Long hotelId, Long RoomCategoryId);


}
