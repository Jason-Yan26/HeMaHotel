package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

public interface WarehouseService {

    /** 获取酒店仓库全部信息*/
    public ResponseUtils getAll(Long userId);

    /** 仓库清仓*/
    public ResponseUtils clear(Long userId);

    /**仓库新增物品*/
    public ResponseUtils add(Long userId,String itemName,Long itemNumber);

    /**仓库物品数量修改*/
    public ResponseUtils modify(Long userId,String itemName,Long itemNumber);


    public ResponseUtils use(Long adminId,String itemName,Long itemNumber);

}
