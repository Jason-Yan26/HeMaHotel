package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.*;
import com.example.hemahotel.entity.*;
import com.example.hemahotel.service.OrderService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;

    @Override
    public ResponseUtils getAllInformation(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        if(!orders.isEmpty()){
            List<JSONObject> orderInformation = new ArrayList<>();
            for(Order o:orders)
                if(!o.getStatus().equals(0)&&!o.getStatus().equals(3)) {//排除已删除/已取消订单
                    JSONObject jsonObject = new JSONObject();


                    //获取订单的状态、创建时间、完成时间、预订房间数量
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    jsonObject.put("createTime",simpleDateFormat.format(o.getCreateTime()));
                    if(o.getCompleteTime() != null)
                        jsonObject.put("completeTime",simpleDateFormat.format(o.getCompleteTime()));

                    jsonObject.put("orderId",o.getId());

                    Long roomCategoryId = o.getCategoryId();
                    RoomCategory rc = roomCategoryRepository.findById(roomCategoryId).get();
                    jsonObject.put("roomCategoryName",rc.getName());
                    jsonObject.put("roomCategoryPrice",rc.getPrice());
                    jsonObject.put("roomNum",o.getNumber());
                    Long hotelId=rc.getHotelId();
                    Hotel h = hotelRepository.findById(hotelId).get();
                    jsonObject.put("hotelName",h.getName());
                    jsonObject.put("hotelPictureUrl",h.getPicture());
                    jsonObject.put("hotelLocation",h.getLocation());
                    jsonObject.put("status",o.getStatus());

                    List<Reservation> reservations = reservationRepository.findByOrderId(o.getId());

                    List<String> roomNames = new ArrayList<>();
                    for(Reservation reservation: reservations){
                        Long roomId = reservation.getRoomId();
                        String roomName = roomRepository.getById(roomId).getName();
                        roomNames.add(roomName);
                    }

                    jsonObject.put("roomNames",roomNames);
                    jsonObject.put("startTime",reservations.get(0).getStartTime());
                    jsonObject.put("endTime",reservations.get(0).getEndTime());

                    orderInformation.add(jsonObject);
                }
            return ResponseUtils.success("订单信息获取成功",  orderInformation);
        }
        else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            return ResponseUtils.response(400,"订单信息为空", jsonObject);
        }
    }

    @Override
    public ResponseUtils getById(Long userId, Long orderId) {

        Optional<Order> o = orderRepository.findByIdAndUserId(orderId,userId);
        JSONObject jsonObject = new JSONObject();

        if(o.isPresent()){
            Order order=o.get();

            //获取订单的状态、创建时间、完成时间、预订房间数量
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            jsonObject.put("createTime",simpleDateFormat.format(order.getCreateTime()));
            if(order.getCompleteTime() != null)
                jsonObject.put("completeTime",simpleDateFormat.format(order.getCompleteTime()));

            jsonObject.put("roomNum",order.getNumber());
            jsonObject.put("status",order.getStatus());

            //获取房间类型名称
            Long roomCategoryId = order.getCategoryId();
            RoomCategory rc = roomCategoryRepository.findById(roomCategoryId).get();
            jsonObject.put("roomCategoryName",rc.getName());
            jsonObject.put("roomCategoryPrice",rc.getPrice());

            //获取酒店的名称、地址和图片
            Long hotelId = rc.getHotelId();
            Hotel h = hotelRepository.findById(hotelId).get();
            jsonObject.put("hotelName",h.getName());
            jsonObject.put("hotelPictureUrl",h.getPicture());
            jsonObject.put("hotelLocation",h.getLocation());

            List<Reservation> reservations = reservationRepository.findByOrderId(orderId);

            List<String> roomNames = new ArrayList<>();
            for(Reservation reservation: reservations){
                Long roomId = reservation.getRoomId();
                String roomName = roomRepository.getById(roomId).getName();
                roomNames.add(roomName);
            }

            jsonObject.put("roomNames",roomNames);
            jsonObject.put("startTime",reservations.get(0).getStartTime());
            jsonObject.put("endTime",reservations.get(0).getEndTime());

            return ResponseUtils.success("订单信息获取成功", jsonObject);
        }
        else {
            jsonObject.put("userId", userId);
            return ResponseUtils.response(400,"订单信息为空", jsonObject);
        }
    }

    @Override
    public ResponseUtils getHotelBookingInformation(Long adminId, Long hotelId) {

        User user = userRepository.getById(adminId);
        JSONObject jsonObject = new JSONObject();

        if(!user.getIdentity().equals(2)){ // 前台人员：2
            jsonObject.put("adminId", adminId);
            return ResponseUtils.response(400,"不存在查看权限", jsonObject);
        }
        else {
            Optional<Hotel> h = hotelRepository.findById(hotelId);
            if (!h.isPresent()) {
                jsonObject.put("hotelId", hotelId);
                return ResponseUtils.response(401, "该酒店不存在", jsonObject);
            } else {

                List<RoomCategory> rcs = roomCategoryRepository.findByHotelId(hotelId);
                List<Long> rcIds = new ArrayList<>();
                for(RoomCategory rc:rcs){
                    rcIds.add(rc.getId());
                }
                List<Order> orders = orderRepository.findByCategoryIdIn(rcIds);//所有订单

                int bookNum = 0;
                double money = 0;
                List<JSONObject> bookInformation = new ArrayList<>();
                for (Order order : orders) {
                    JSONObject info = new JSONObject();
                    info.put("orderId", order.getId());
                    info.put("bookUserId", order.getUserId());
                    Long categoryId = order.getCategoryId();
                    info.put("roomCategoryId", categoryId);
                    RoomCategory rc = roomCategoryRepository.getById(categoryId);
                    info.put("roomCategory", rc.getName());
                    info.put("roomNum", order.getNumber());
                    bookNum += order.getNumber();
                    money += order.getPaymentMoney();
                    List<Reservation> reservations = reservationRepository.findByOrderId(order.getId());

                    List<String> roomNames = new ArrayList<>();
                    for (Reservation reservation : reservations) {
                        Long roomId = reservation.getRoomId();
                        String roomName = roomRepository.getById(roomId).getName();
                        roomNames.add(roomName);
                    }
                    info.put("roomNames", roomNames);
                    info.put("guestId1", reservations.get(0).getGuestId1());
                    info.put("guestId2", reservations.get(0).getGuestId2());
                    info.put("guestId3", reservations.get(0).getGuestId3());
                    info.put("roomStartTime", reservations.get(0).getStartTime());
                    info.put("roomEndTime", reservations.get(0).getEndTime());
                    info.put("orderCreateTime", order.getCreateTime());
                    info.put("orderCompleteTime", order.getCompleteTime());
                    info.put("orderStatus", order.getStatus());
                    info.put("orderMoney", order.getPaymentMoney());
                    info.put("payType", order.getPaymentType());
                    bookInformation.add(info);
                }

                jsonObject.put("bookNum", bookNum);
                jsonObject.put("freeNum", roomRepository.countByRoomCategoryIdIn(rcIds) - bookNum);
                jsonObject.put("money", money);
                jsonObject.put("orders", bookInformation);
                return ResponseUtils.response(200,"酒店的房间预定情况查看成功", jsonObject);
            }
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


    /** 获取累计订单数量*/
    public ResponseUtils getTotalNumber(Long userId){
        JSONObject jsonObject = new JSONObject();
        User user = userRepository.findById(userId).get();
        //确保该用户身份为系统管理员，才有权限可以操作
        if(user.getIdentity() == 1){
            long totalNumber = orderRepository.count();
            jsonObject.put("totalNumber",totalNumber);
            return ResponseUtils.response(200, "累计订单数量获取成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取订单信息", jsonObject);
        }
    }

    /** 获取今日订单数量*/
    public ResponseUtils getTodayNumber(Long userId){
        JSONObject jsonObject = new JSONObject();
        User user = userRepository.findById(userId).get();
        //确保该用户身份为系统管理员，才有权限可以操作
        if(user.getIdentity() == 1){

            //今日0点时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Timestamp startTime = new Timestamp(calendar.getTimeInMillis());

            //今日23.59时间
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Timestamp endTime = new Timestamp(calendar.getTimeInMillis());

            int todayNumber = orderRepository.countAllByCreateTimeBetween(startTime,endTime);
            jsonObject.put("todayNumber",todayNumber);

            return ResponseUtils.response(200, "今日订单数量获取成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取订单信息", jsonObject);
        }
    }

    /** 获取昨日订单数量*/
    public ResponseUtils getYesterdayNumber(Long userId){
        JSONObject jsonObject = new JSONObject();
        User user = userRepository.findById(userId).get();
        //确保该用户身份为系统管理员，才有权限可以操作
        if(user.getIdentity() == 1){

            //昨日00：00时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Timestamp startTime = new Timestamp(calendar.getTimeInMillis());

            //昨日23:59时间
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Timestamp endTime = new Timestamp(calendar.getTimeInMillis());


            int yesterdayNumber = orderRepository.countAllByCreateTimeBetween(startTime,endTime);
            jsonObject.put("yesterdayNumber",yesterdayNumber);

            return ResponseUtils.response(200, "昨日订单数量获取成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取订单信息", jsonObject);
        }
    }

    /** 获取销售额*/
    public ResponseUtils getSalesAmount(Long userId,Integer startYear,Integer startMonth,Integer startDay,Integer endYear,Integer endMonth,Integer endDay){
        JSONObject jsonObject = new JSONObject();
        User user = userRepository.findById(userId).get();
        //确保该用户身份为系统管理员，才有权限可以操作
        if(user.getIdentity() == 1){

            //销售额查询开始时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, startYear);
            calendar.set(Calendar.MONTH, startMonth-1);
            calendar.set(Calendar.DAY_OF_MONTH, startDay);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Timestamp startTime = new Timestamp(calendar.getTimeInMillis());

            //销售额查询结束时间
            calendar.set(Calendar.YEAR, endYear);
            calendar.set(Calendar.MONTH, endMonth-1);
            calendar.set(Calendar.DAY_OF_MONTH, endDay);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Timestamp endTime = new Timestamp(calendar.getTimeInMillis());

            System.out.println(startTime);
            System.out.println(endTime);

            List<Order> orders = orderRepository.findByCompleteTimeBetween(startTime,endTime);
            double salesAmount = 0;
            for(Order order:orders){
                salesAmount += order.getPaymentMoney();
            }

            jsonObject.put("salesAmount",salesAmount);

            return ResponseUtils.response(200, "销售额查询成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取订单信息", jsonObject);
        }
    }

    /** 获取交易用户数*/
    public ResponseUtils getUsersAmount(Long userId,Integer type){
        JSONObject jsonObject = new JSONObject();
        User user = userRepository.findById(userId).get();
        //确保该用户身份为系统管理员，才有权限可以操作
        if(user.getIdentity() == 1){

            long amount = 0;
            //累计
            if(type == 0){
                amount = orderRepository.count();
            }
            //今天
            else if(type == 1) {
                //今日0点时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Timestamp startTime = new Timestamp(calendar.getTimeInMillis());

                //今日23.59时间
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Timestamp endTime = new Timestamp(calendar.getTimeInMillis());

                amount = orderRepository.countAllByCreateTimeBetween(startTime,endTime);
            }
            jsonObject.put("usersAmount",amount);

            return ResponseUtils.response(200, "交易用户数量查询成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取订单信息", jsonObject);
        }
    }



}
