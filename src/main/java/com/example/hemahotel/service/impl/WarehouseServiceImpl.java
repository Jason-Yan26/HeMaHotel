package com.example.hemahotel.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.dao.WarehouseRepository;
import com.example.hemahotel.entity.User;
import com.example.hemahotel.entity.Warehouse;
import com.example.hemahotel.service.WarehouseService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class WarehouseServiceImpl implements WarehouseService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    /** 获取酒店仓库全部信息*/
    public ResponseUtils getAll(Long userId){
        JSONObject jsonObject = new JSONObject();

        User user = userRepository.findById(userId).get();
        //确保该用户身份为仓库管理员，才有权限可以操作
        if(user.getIdentity() == 4){
            Long hotelId = user.getHotelId();
            List<Warehouse> warehouses = warehouseRepository.findByHotelId(hotelId);
            jsonObject.put("warehouses",warehouses);
            return ResponseUtils.response(200, "酒店仓库信息获取成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取酒店仓库信息", jsonObject);
        }
    }


    /** 仓库清仓*/
    public ResponseUtils clear(Long userId){
        JSONObject jsonObject = new JSONObject();

        User user = userRepository.findById(userId).get();
        //确保该用户身份为仓库管理员，才有权限可以操作
        if(user.getIdentity() == 4){
            Long hotelId = user.getHotelId();
            warehouseRepository.deleteAllByHotelId(hotelId);

            return ResponseUtils.response(200, "仓库清仓成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取酒店仓库信息", jsonObject);
        }
    }

    /**仓库新增物品*/
    public ResponseUtils add(Long userId,String itemName,Long itemNumber){
        JSONObject jsonObject = new JSONObject();

        User user = userRepository.findById(userId).get();
        //确保该用户身份为仓库管理员，才有权限可以操作
        if(user.getIdentity() == 4){
            Long hotelId = user.getHotelId();
            Warehouse warehouse = new Warehouse(hotelId,itemName,itemNumber);

            warehouseRepository.save(warehouse);

            return ResponseUtils.response(200, "仓库新增物品成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取酒店仓库信息", jsonObject);
        }
    }

    /**仓库物品数量修改*/
    public ResponseUtils modify(Long userId,String itemName,Long itemNumber){
        JSONObject jsonObject = new JSONObject();

        User user = userRepository.findById(userId).get();
        //确保该用户身份为仓库管理员，才有权限可以操作
        if(user.getIdentity() == 4){
            Long hotelId = user.getHotelId();

            Warehouse warehouse = warehouseRepository.findByHotelIdAndItemName(hotelId,itemName).get();
            warehouse.setItemNumber(itemNumber);
            warehouseRepository.save(warehouse);

            return ResponseUtils.response(200, "仓库物品数量修改成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取酒店仓库信息", jsonObject);
        }

    }

    @Override
    public ResponseUtils use(Long adminId, String itemName, Long itemNumber) {
        JSONObject jsonObject = new JSONObject();

        Optional<User> u = userRepository.findById(adminId);
        if(u.isPresent()){
            User user= u.get();
            //确保该用户身份为清洁人员，才有权限可以更换房间用品
            if(user.getIdentity().equals(3)){
                Long hotelId = user.getHotelId();

                Optional<Warehouse> w = warehouseRepository.findByHotelIdAndItemName(hotelId,itemName);
                if(w.isPresent()){
                    Warehouse warehouse = w.get();
                    if(warehouse.getItemNumber()>=itemNumber) {
                        warehouse.setItemNumber(warehouse.getItemNumber() - itemNumber);
                        warehouseRepository.save(warehouse);
                        jsonObject.put("item", w);
                        return ResponseUtils.response(200, "更换房间用品成功", jsonObject);
                    }else{
                        jsonObject.put("item", w);
                        return ResponseUtils.response(403, "房间用品库存不足", jsonObject);
                    }
                }else{
                    jsonObject.put("itemName",itemName);
                    return ResponseUtils.response(402, "该房间用品不存在", jsonObject);
                }
            }
            else{
                jsonObject.put("identity",user.getIdentity());
                return ResponseUtils.response(401, "权限不足，无法更换房间用品", jsonObject);
            }
        }else {
            jsonObject.put("adminId",adminId);
            return ResponseUtils.response(400, "该用户不存在", jsonObject);
        }

    }
}
