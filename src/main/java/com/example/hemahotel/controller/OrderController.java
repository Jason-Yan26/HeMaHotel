package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.credentials.http.HttpRequest;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.OrderService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
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

    @PostMapping("/getById")
    public ResponseUtils getById(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));
        Long orderId = jsonObject.getLong("orderId");

        return orderService.getById(userId,orderId);
    }

    /** 订单删除接口 */
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

        // 客房类型、预订数量、预订开始时间、预订结束时间
        Long roomCategoryId = jsonObject.getLong("roomCategoryId");
        Integer num = jsonObject.getInteger("number");
        Date startTime = Date.valueOf(jsonObject.getString("startTime"));
        Date endTime = Date.valueOf(jsonObject.getString("endTime"));

        return orderService.createOrder(id,roomCategoryId,num,startTime,endTime);
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

    /** 获取累计订单数量接口 */
    @PostMapping("/number/total")
    public ResponseUtils getTotalNumber(HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        return orderService.getTotalNumber(userId);
    }

    /** 获取当日订单数量接口 */
    @PostMapping("/number/today")
    public ResponseUtils getTodayNumber(HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        return orderService.getTodayNumber(userId);
    }

    /** 获取昨日订单数量接口 */
    @PostMapping("/number/yesterday")
    public ResponseUtils getYesterdayNumber(HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        return orderService.getYesterdayNumber(userId);
    }

    /** 查询销售额 */
    @PostMapping("/salesAmount")
    public ResponseUtils getSalesAmount(@RequestBody JSONObject jsonObject,HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        Integer startYear = jsonObject.getInteger("startYear");
        Integer startMonth = jsonObject.getInteger("startMonth");
        Integer startDay = jsonObject.getInteger("startDay");

        Integer endYear = jsonObject.getInteger("endYear");
        Integer endMonth = jsonObject.getInteger("endMonth");
        Integer endDay = jsonObject.getInteger("endDay");

        return orderService.getSalesAmount(userId,startYear,startMonth,startDay,endYear,endMonth,endDay);
    }

    /** 查询交易用户数量*/
    @PostMapping("/usersAmount")
    public ResponseUtils getUserssAmount(@RequestBody JSONObject jsonObject,HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        //查询类型：type = 0:累计;type = 1:今日
        Integer type = jsonObject.getInteger("type");

        return orderService.getUsersAmount(userId,type);
    }

}
