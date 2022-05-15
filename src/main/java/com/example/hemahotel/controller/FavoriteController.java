package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.FavoriteRepository;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.FavoriteService;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user/favorite")
@CrossOrigin(origins = "*")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    //收藏夹添加一家酒店接口
    @PostMapping("/add")
    public ResponseUtils add(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        //从token中获取用户id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        Long hotelId = jsonObject.getLong("hotelId");
        return favoriteService.add(id, hotelId);
    }

    //收藏夹移除一家酒店接口
    @PostMapping("/remove")
    public ResponseUtils remove(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        //从token中获取用户id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        Long productId = jsonObject.getLong("hotelId");
        return favoriteService.remove(id, productId);
    }

    //收藏夹获取所有酒店接口
    @PostMapping("/get")
    public ResponseUtils get(HttpServletRequest request) {

        //从token中获取用户id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        return favoriteService.get(id);
    }
}
