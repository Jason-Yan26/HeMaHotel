package com.example.hemahotel.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.WarehouseService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/warehouse")
@Slf4j
@CrossOrigin(origins = "*")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    /**仓库物品查询接口*/
    @PostMapping("/get/all")
    public ResponseUtils getAll(HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        return warehouseService.getAll(userId);
    }

    /**仓库清仓*/
    @PostMapping("/clear")
    public ResponseUtils clear(HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        return warehouseService.clear(userId);
    }

    /**仓库新增物品*/
    @PostMapping("/add")
    public ResponseUtils add(@RequestBody JSONObject jsonObject,HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        String itemName = jsonObject.getString("itemName");
        Long itemNumber = jsonObject.getLong("itemNumber");

        return warehouseService.add(userId,itemName,itemNumber);
    }

    /**仓库物品数量修改*/
    @PostMapping("/modify")
    public ResponseUtils modify(@RequestBody JSONObject jsonObject,HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        //修改的物品的名称和修改后的数量
        String itemName = jsonObject.getString("itemName");
        Long itemNumber = jsonObject.getLong("itemNumber");

        return warehouseService.modify(userId,itemName,itemNumber);
    }

}
