package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.GuestRepository;
import com.example.hemahotel.entity.Guest;
import com.example.hemahotel.service.GuestService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private GuestRepository guestRepository;

    private JSONObject jsonObject;

    /**增加住客*/
    public ResponseUtils addGuest(Long id, String guestName, String guestPhone, String guestIdNumber){
        jsonObject = new JSONObject();

        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        Timestamp updateTime = new Timestamp(System.currentTimeMillis());

        Guest guest = guestRepository.save(new Guest(id,guestName,guestPhone,guestIdNumber,createTime,updateTime));

        jsonObject.put("id",guest.getId());
        jsonObject.put("name",guest.getName());
        jsonObject.put("phone",guest.getPhone());
        jsonObject.put("idNumber",guest.getIdNumber());

        return ResponseUtils.response(200,"住客增加成功", jsonObject);

    }

    /**删除住客*/
    public ResponseUtils deleteGuest(Long userId,Long guestId){

        Optional<Guest> g = guestRepository.findById(guestId);

        //guestId不存在，删除失败
        if(!g.isPresent())
            return ResponseUtils.response(404,"住客删除失败", jsonObject);
        else{
            Guest guest = g.get();
            if(guest.getUserId() == userId){
                guestRepository.deleteById(guestId);
                return ResponseUtils.response(200,"住客删除成功", jsonObject);
            }
            //userId 和 guestId 不匹配，删除失败
            else
                return ResponseUtils.response(404,"住客删除失败", jsonObject);
        }
    }

    /**修改住客信息*/
    public ResponseUtils modifyGuest(Long userId,Long guestId,String guestName,String guestPhone,String guestIdNumber){
        Optional<Guest> g = guestRepository.findById(guestId);
        jsonObject = new JSONObject();

        //guestId不存在，删除失败
        if(!g.isPresent())
            return ResponseUtils.response(404,"住客信息修改失败", jsonObject);
        else{
            Guest guest = g.get();
            if(guest.getUserId() == userId){
                Timestamp updateTime = new Timestamp(System.currentTimeMillis());

                guest.setName(guestName);
                guest.setPhone(guestPhone);
                guest.setIdNumber(guestIdNumber);
                guest.setUpdateTime(updateTime);

                guestRepository.save(guest);
                return ResponseUtils.response(200,"住客信息修改成功", jsonObject);

            }
            //userId 和 guestId 不匹配，删除失败
            else
                return ResponseUtils.response(404,"住客信息修改失败", jsonObject);
        }
    }
}
