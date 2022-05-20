package com.example.hemahotel.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.GuestService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/guest")
@Slf4j
@CrossOrigin(origins = "*")
public class GuestController {

    @Autowired
    private GuestService guestService;


    /**查询住客接口*/
    @PostMapping("/get")
    public ResponseUtils getGuest(HttpServletRequest request) {
        //从token中获取id
        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        return guestService.getGuest(userId);
    }

    /**增加住客接口*/
    @PostMapping("/add")
    public ResponseUtils addGuest(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        //从token中获取id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        String guestName = jsonObject.getString("name");
        String guestPhone = jsonObject.getString("phone");
        String guestIdNumber = jsonObject.getString("idNumber");

        return guestService.addGuest(id,guestName,guestPhone,guestIdNumber);
    }

    /**删除住客接口*/
    @PostMapping("/delete")
    public ResponseUtils deleteGuest(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        //从token中获取id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        Long guestId = jsonObject.getLong("guestId");

        return guestService.deleteGuest(id,guestId);
    }

    /**修改住客信息接口*/
    @PostMapping("/modify")
    public ResponseUtils modifyGuest(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        //从token中获取id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        Long guestId = jsonObject.getLong("id");
        String guestName = jsonObject.getString("name");
        String guestPhone = jsonObject.getString("phone");
        String guestIdNumber = jsonObject.getString("idNumber");

        return guestService.modifyGuest(id,guestId,guestName,guestPhone,guestIdNumber);
    }

}
