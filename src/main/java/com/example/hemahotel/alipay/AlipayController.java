package com.example.hemahotel.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.example.hemahotel.dao.GuestRepository;
import com.example.hemahotel.dao.OrderRepository;
import com.example.hemahotel.dao.ReservationRepository;
import com.example.hemahotel.entity.Guest;
import com.example.hemahotel.entity.Order;
import com.example.hemahotel.entity.Reservation;
import com.example.hemahotel.jwt.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/api/alipay")
public class AlipayController {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @PostMapping("/topay")
    @ResponseBody
    public String toPay(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {

        //从token中获取id
        String token = request.getHeader("token");
        Long userId = Long.valueOf(JWTUtils.getUserId(token));

        Long orderId = jsonObject.getLong("orderId");
        String paymentType = jsonObject.getString("PaymentType");

        //设置订单的支付方式
        Order order = orderRepository.findById(orderId).get();
        order.setPaymentType(paymentType);
        orderRepository.save(order);

        //该订单预订的房间数量
        int roomNumbers = order.getNumber().intValue();

        //设置每间预订房间的住客(最多3人)
        //例如第2间住客的id:guestId21、guestId22、guestId23
        for(int i = 1; i <= roomNumbers;i++){
            Long guestId1 = jsonObject.getLong("guestId" + String.valueOf(i) + "1");
            Long guestId2 = jsonObject.getLong("guestId" + String.valueOf(i) + "2");
            Long guestId3 = jsonObject.getLong("guestId" + String.valueOf(i) + "3");

            Long orderIthRoom = new Long((long)i);

            Reservation reservation = reservationRepository.findByOrderIdAndOrderIthRoom(orderId,orderIthRoom).get();
            reservation.setGuestId1(guestId1);
            reservation.setGuestId2(guestId2);
            reservation.setGuestId3(guestId3);
            reservationRepository.save(reservation);
        }

        String paymentMoney = String.format("%.2f", order.getPaymentMoney()).toString();
        return alipayService.toPay(orderId.toString(),paymentMoney);
    }

    @PostMapping("/callback")
    @ResponseBody
    public String notifyCallback(HttpServletRequest request) throws Exception {
        System.out.println("进入异步");
        String success = "success";
        String failure = "failure";

        // https://opendocs.alipay.com/open/54/00y8k9 新老版本说明中有异步通知的新版说明
        // 获取支付宝异步回调信息, 将其转为 Map<String, String>
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }

        System.out.println(params);

        Order order = orderRepository.findById(Long.valueOf(params.get("out_trade_no"))).get();
        Timestamp CompleteTime = new Timestamp(System.currentTimeMillis());
        order.setStatus(2);
        order.setCompleteTime(CompleteTime);
        orderRepository.save(order);

        // 新版 SDK 不用移除 sign_type
        // params.remove("sign_type");

        // 验签
        boolean signVerified = Factory.Payment.Common().verifyNotify(params);

        if(signVerified){ // 验签通过
//            System.out.println("通过验签");
            return success;
        }else{ // 验签失败
            return failure;
        }
    }

    @GetMapping("/query")
    @ResponseBody
    public Object queryTradeStatus(String outTradeNo) throws Exception {
        Object result = alipayService.queryTradeStatus(outTradeNo);
        return result;
    }
}