package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

public interface GuestService {

    /**查询住客*/
    public ResponseUtils getGuest(Long uesrId);

    public ResponseUtils getGuestInformation(Long adminId, Long guestId);

    /**增加住客*/
    public ResponseUtils addGuest(Long id,String guestName,String guestPhone,String guestIdNumber);

    /**删除住客*/
    public ResponseUtils deleteGuest(Long id,Long guestId);

    /**修改住客信息*/
    public ResponseUtils modifyGuest(Long id,Long guestId,String guestName,String guestPhone,String guestIdNumber);

}
