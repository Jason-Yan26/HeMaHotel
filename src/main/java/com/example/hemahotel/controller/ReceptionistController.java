package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.GuestService;
import com.example.hemahotel.service.OrderService;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@CrossOrigin(origins = "*")
public class ReceptionistController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private GuestService guestService;

    @Autowired
    private UserService userService;

    @PostMapping("/hotelBooking")
    public ResponseUtils getHotelBookingInformation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long adminId = Long.valueOf(JWTUtils.getUserId(token)); // admin status

        Long hotelId = jsonObject.getLong("hotelId");

        return orderService.getHotelBookingInformation(adminId, hotelId);

    }

    @PostMapping("/userInformation")
    public ResponseUtils getUserInformation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long adminId = Long.valueOf(JWTUtils.getUserId(token)); // admin status

        Long userId = jsonObject.getLong("userId");

        return userService.getUserInformation(adminId, userId);
    }

    @PostMapping("/guestInformation")
    public ResponseUtils getGuestInformation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long adminId = Long.valueOf(JWTUtils.getUserId(token)); // admin status

        Long guestId = jsonObject.getLong("guestId");

        return guestService.getGuestInformation(adminId, guestId);
    }

    @PostMapping("/checkIn")
    public ResponseUtils checkIn(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long adminId = Long.valueOf(JWTUtils.getUserId(token)); // admin status

        String guestID1 = jsonObject.getString("guestID1"); //身份证号
        String guestID2 = jsonObject.getString("guestID2");
        String guestID3 = jsonObject.getString("guestID3");
        String reservationTelephone = jsonObject.getString("telephone");//预定人电话号码


        return orderService.checkIn(adminId, reservationTelephone, guestID1, guestID2, guestID3);
    }
    @PostMapping("/checkOut")
    public ResponseUtils checkOut(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long adminId = Long.valueOf(JWTUtils.getUserId(token)); // admin status

        Long roomID = jsonObject.getLong("roomID"); //房号

        return orderService.checkOut(adminId, roomID);
    }
}