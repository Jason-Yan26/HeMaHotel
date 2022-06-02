package com.example.hemahotel.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.ReservationRepository;
import com.example.hemahotel.entity.Reservation;
import com.example.hemahotel.entity.Room;
import com.example.hemahotel.dao.RoomRepository;
import com.example.hemahotel.service.RoomService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public ResponseUtils getFreeNumByRoomCategoryId(Long roomCategoryId) {

        List<Room> rooms = roomRepository.findByRoomCategoryIdAndStatus(roomCategoryId,0); // 客房状态,0：空闲
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("freeNum", rooms.size());
        return ResponseUtils.response(200,"查找成功", jsonObject);

    }

    @Override
    public ResponseUtils clean(Long adminId, Long roomId) {
        Optional<Room> r = roomRepository.findById(roomId);
        JSONObject jsonObject = new JSONObject();
        if(r.isPresent()) {
            Room room = r.get();
            Integer status = room.getStatus();
            if (status.equals(2)) {
                room.setStatus(0);
                roomRepository.save(room);
                return ResponseUtils.response(200, "清理房间成功", jsonObject);
            } else{
                jsonObject.put("roomStatus",status);
                return ResponseUtils.response(401, "房间无需清理", jsonObject);
            }
        }else{
            return ResponseUtils.response(400, "房间不存在", jsonObject);
        }
    }

}
