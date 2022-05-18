package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

import java.sql.Date;
import java.sql.Timestamp;

public interface OrderService {
    public ResponseUtils getAllInformation(Long userId);

    public ResponseUtils deleteInformationById(Long userId, Long orderId);

    public ResponseUtils createOrder(Long userId, Long roomCategoryId, Integer reservationNum, Date startTime,Date endTime);

    public ResponseUtils toPayOrder(Long userId, Long orderId, String payType);
}
