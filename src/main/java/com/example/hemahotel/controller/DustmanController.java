package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.entity.Warehouse;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.RoomService;
import com.example.hemahotel.service.WarehouseService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@CrossOrigin(origins = "*")
public class DustmanController {

    @Autowired
    RoomService roomService;

    @Autowired
    WarehouseService warehouseService;

    @PostMapping("/clean")
    public ResponseUtils cleanRoom(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long adminId = Long.valueOf(JWTUtils.getUserId(token)); // admin status

        Long roomId = jsonObject.getLong("roomId");

        return roomService.clean(adminId,roomId);
    }
    @PostMapping("/item/use")
    public ResponseUtils useItem(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long adminId = Long.valueOf(JWTUtils.getUserId(token)); // admin status

        //使用的物品名称和使用的数量
        String itemName = jsonObject.getString("itemName");
        Long itemNumber = jsonObject.getLong("itemNumber");

        return warehouseService.use(adminId,itemName,itemNumber);
    }
}
