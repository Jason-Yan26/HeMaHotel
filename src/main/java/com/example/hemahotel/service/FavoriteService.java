package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

public interface FavoriteService {

    /**增加商品到收藏夹*/
    public ResponseUtils add(Long userId, Long hotelId);

    /**从收藏夹中删除商品*/
    public ResponseUtils remove(Long userId, Long hotelId);

    /**查找收藏夹商品*/
    public ResponseUtils get(Long userId);
}
