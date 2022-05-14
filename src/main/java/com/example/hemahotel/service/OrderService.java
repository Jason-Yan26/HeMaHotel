package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

import java.sql.Timestamp;

public interface OrderService {
    ResponseUtils getAllInformation(Long userId);

    ResponseUtils deleteInformationById(Long userId, Long orderId);

    ResponseUtils createOrder(Long userId, Long roomCategory, Timestamp reservationTime, Integer reservationNum);

    ResponseUtils toPayOrder(Long userId, Long orderId, String payType);
}
