package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.OrderRepository;
import com.example.hemahotel.dao.ReservationRepository;
import com.example.hemahotel.dao.RoomCategoryRepository;
import com.example.hemahotel.dao.RoomRepository;
import com.example.hemahotel.entity.Order;
import com.example.hemahotel.entity.Reservation;
import com.example.hemahotel.entity.Room;
import com.example.hemahotel.entity.RoomCategory;
import com.example.hemahotel.service.OrderService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

    @Override
    public ResponseUtils getAllInformation(Long userId) {

        JSONObject jsonObject = new JSONObject();
        List<Order> orders = orderRepository.findByUserId(userId);

        if(!orders.isEmpty()){
            List<Order> orderInformation = new ArrayList<>();
            for(Order o:orders)
                if(!o.getStatus().equals(0)&&!o.getStatus().equals(3))//排除已删除/已取消订单
                    orderInformation.add(o);
            return ResponseUtils.success("订单信息获取成功",  orderInformation);
        }
        else {
            jsonObject.put("userId", userId);
            return ResponseUtils.response(400,"订单信息为空", jsonObject);
        }
    }

    /** 删除一个订单信息 */
    @Override
    public ResponseUtils deleteInformationById(Long userId, Long orderId) {

        JSONObject jsonObject = new JSONObject();
        Optional<Order> o = orderRepository.findByIdAndUserId(orderId,userId);

        if(!o.isPresent()){
            return ResponseUtils.response(400, "该用户指定的订单不存在，请重新操作", jsonObject);
        }
        else {
            Order order = o.get();
            order.setStatus(0); // 更改状态，删除状态为0
            order.setCompleteTime(new Timestamp(System.currentTimeMillis())); // 删除时间点
            orderRepository.save(order);
            return ResponseUtils.response(200, "该订单信息删除成功", jsonObject);
        }
    }

    /** 创建一个订单 */
    @Override
    public ResponseUtils createOrder(Long userId, Long roomCategoryId, Timestamp reservationTime, Integer reservationNum) {

        JSONObject jsonObject = new JSONObject();
        Optional<RoomCategory> rc = roomCategoryRepository.findById(roomCategoryId);

        if(!rc.isPresent()){
            return ResponseUtils.response(400, "订单所选房型不存在，请重新选择", jsonObject);
        }
        else {
            Double price = rc.get().getPrice()*reservationNum;//每间房价格*数量
            Order newOrder=new Order(userId, roomCategoryId,reservationNum, 1, price, reservationTime);//1:未支付
            orderRepository.save(newOrder);
            List<Room> rooms = roomRepository.findByRoomCategoryIdAndStatus(roomCategoryId,0);//返回空闲的房间
            Integer num = 0;
            for(Room room:rooms){
                room.setStatus(1); // 更新房间状态
                roomRepository.save(room);
                Reservation reservation = new Reservation(userId,newOrder.getId(),room.getId(),reservationTime); // 这里面还有guestId
                reservationRepository.save(reservation);
                num+=1;
                if(num.equals(reservationNum))break;
            }
            jsonObject.put("orderId",newOrder.getId());
            return ResponseUtils.response(200, "订单创建成功", jsonObject);
        }
    }

    @Override
    public ResponseUtils toPayOrder(Long userId, Long orderId, String payType) {
        JSONObject jsonObject = new JSONObject();
        Optional<Order> o = orderRepository.findById(orderId);
        if(!o.isPresent()){
            return ResponseUtils.response(400, "订单不存在", jsonObject);
        }
        else{
            Order order=o.get();
            if(!order.getUserId().equals(userId)){ // 非user本人创建的订单
                jsonObject.put("userId",userId);
                return ResponseUtils.response(401, "订单出现错误，订单非用户本人创建", jsonObject);
            }
            else{
                if(!order.getStatus().equals(1)){ // 不是待支付状态
                    jsonObject.put("orderStatus",order.getStatus());
                    return ResponseUtils.response(401, "订单不是待支付状态", jsonObject);
                }
                else {
                    jsonObject.put("payType",payType);
                    return ResponseUtils.response(200, "订单跳转支付页面", jsonObject);
                }
            }
        }
    }

}
