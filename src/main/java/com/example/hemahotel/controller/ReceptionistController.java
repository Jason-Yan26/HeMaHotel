package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.OrderService;
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

    @PostMapping("/hotelBooking")
    public ResponseUtils getHotelBookingInformation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long adminId = Long.valueOf(JWTUtils.getUserId(token)); // admin status

        Long hotelId = jsonObject.getLong("hotelId");

        return orderService.getHotelBookingInformation(adminId, hotelId);

    }
}
