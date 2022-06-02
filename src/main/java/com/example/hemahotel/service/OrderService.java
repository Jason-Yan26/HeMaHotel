package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

import java.sql.Date;
import java.sql.Timestamp;

public interface OrderService {
    public ResponseUtils getAllInformation(Long userId);

    public ResponseUtils getById(Long userId,Long orderId);

    public ResponseUtils getHotelBookingInformation(Long adminId,Long hotelId);

    public ResponseUtils deleteInformationById(Long userId, Long orderId);

    public ResponseUtils createOrder(Long userId, Long roomCategoryId, Integer reservationNum, Date startTime,Date endTime);

    public ResponseUtils toPayOrder(Long userId, Long orderId, String payType);

    /** 获取累计订单数量*/
    public ResponseUtils getTotalNumber(Long userId);

    /** 获取今日订单数量*/
    public ResponseUtils getTodayNumber(Long userId);

    /** 获取昨日订单数量*/
    public ResponseUtils getYesterdayNumber(Long userId);

    /** 获取销售额*/
    public ResponseUtils getSalesAmount(Long userId,Integer startYear,Integer startMonth,Integer startDay,Integer endYear,Integer endMonth,Integer endDay);

    /** 获取交易用户数*/
    public ResponseUtils getUsersAmount(Long userId,Integer type);
}
