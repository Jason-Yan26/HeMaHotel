package com.example.hemahotel.service;

import com.example.hemahotel.entity.Room;
import com.example.hemahotel.utils.ResponseUtils;

import java.util.List;

public interface RoomService {

    public ResponseUtils getFreeNumByRoomCategoryId(Long roomCategoryId);

    public ResponseUtils clean(Long adminId, Long roomId);
}
