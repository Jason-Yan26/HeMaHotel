package com.example.hemahotel.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.entity.Room;
import com.example.hemahotel.dao.RoomRepository;
import com.example.hemahotel.service.RoomService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Override
    public ResponseUtils getFreeNumByRoomCategoryId(Long roomCategoryId) {

        List<Room> rooms = roomRepository.findByRoomCategoryIdAndStatus(roomCategoryId,0); // 客房状态,0：空闲
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("freeNum", rooms.size());
        return ResponseUtils.response(200,"查找成功", jsonObject);

    }

}
