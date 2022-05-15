package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.credentials.http.HttpRequest;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.OrderService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** 获取用户全部订单信息 */
    @PostMapping("/information/all")
    public ResponseUtils getAllInformation(HttpServletRequest request) {

        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));
        return orderService.getAllInformation(id);
    }

    /** 订单删除窗口 */
    @PostMapping("/delete")
    public ResponseUtils deleteInformationById(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));
        Long orderId = jsonObject.getLong("orderId");

        return orderService.deleteInformationById(id,orderId);
    }

    /** 订单创建接口 */
    @PostMapping("/create")
    public ResponseUtils createOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        // 客房类型、预订数量、预订时间
        Long roomCategoryId = jsonObject.getLong("roomCategoryId");
        Integer num = jsonObject.getInteger("number");
        Timestamp reservationTime = new Timestamp(System.currentTimeMillis());

        return orderService.createOrder(id,roomCategoryId,reservationTime,num);
    }

    /** 订单支付接口 */
    @PostMapping("/toPay")
    public ResponseUtils toPayOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        //订单id、支付类型
        Long orderId = jsonObject.getLong("orderId");
        String payType=jsonObject.getString("payType");

        return orderService.toPayOrder(id,orderId,payType);
    }

}
