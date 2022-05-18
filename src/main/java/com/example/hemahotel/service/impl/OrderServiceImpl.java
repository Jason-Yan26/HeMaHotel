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

import java.sql.Date;
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

    /** 创建订单 */
    public ResponseUtils createOrder(Long userId, Long roomCategoryId, Integer reservationNum, Date startTime, Date endTime){

        JSONObject jsonObject = new JSONObject();

        Optional<RoomCategory> rc = roomCategoryRepository.findById(roomCategoryId);

        if(!rc.isPresent()){
            return ResponseUtils.response(400, "订单所选房型不存在，请重新选择", jsonObject);
        }
        else {
            //找到属于该roomCategory的所有room
            List<Room> rooms = roomRepository.findByRoomCategoryId(roomCategoryId);
            List<Long> roomIds = new ArrayList<>();//可以预订的客房的id


            int room_can_reserve = 0;//可以预订的房间数量

            for(Room room:rooms){
                List<Reservation> reservations = reservationRepository.findByRoomId(room.getId());

                int conflict = 0;

                for(Reservation reservation:reservations){
                    Date startTime_Reserved = reservation.getStartTime();
                    Date endTime_Reserved = reservation.getEndTime();

                    //如果该房间已有的预订的结束时间小于等于 当前预订的开始时间
                    //或 已有的预订的开始时间 大于等于 当前预订的结束时间，则该房间可以被预订

                    if(!(endTime_Reserved.before(startTime) || endTime_Reserved.equals(startTime)
                    || startTime_Reserved.after(endTime) || startTime_Reserved.equals(endTime))){
                        conflict++;
                        break;
                    }
                }
                //如果该房间已有的预订和当前预订的时间不冲突，则该房间可以预订。
                if(conflict == 0) {
                    roomIds.add(room.getId());
                    room_can_reserve++;
                }

                if(room_can_reserve == reservationNum)
                    break;
            }

            //当前剩余房间满足预订需求
            if(room_can_reserve == reservationNum){

                Double price = rc.get().getPrice()*reservationNum;//每间房价格*数量
                Timestamp createTime = new Timestamp(System.currentTimeMillis());
                Order newOrder=new Order(userId, roomCategoryId,reservationNum, 1, price, createTime);//status=1:未支付
                orderRepository.save(newOrder);

                int ithRoom = 1;
                for(Long roomId:roomIds){
                    reservationRepository.save(new Reservation(userId,newOrder.getId(),roomId,Long.valueOf(ithRoom),startTime,endTime));
                    ithRoom++;
                }

                jsonObject.put("orderId",newOrder.getId());
                return ResponseUtils.response(200, "订单创建成功", jsonObject);


            }
            //当前剩余房间数量不足
            else{
                return ResponseUtils.response(401, "该类型客房剩余"+room_can_reserve+"间，无法满足预订需求！", jsonObject);
            }
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
