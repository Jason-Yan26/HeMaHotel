package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.ForumRepository;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.ForumService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private ForumRepository forumRepository;

    //发帖
    @PostMapping("/add")
    public ResponseUtils add(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        //从token中获取用户id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        String content = jsonObject.getString("content");
        String title = jsonObject.getString("title");

        return  forumService.add(id,title,content);
    }

    //查询所有帖子信息
    @PostMapping("/getAll")
    public ResponseUtils getForumNum(@RequestBody JSONObject jsonObject) {

        int pageIndex = jsonObject.getInteger("pageIndex"); //当前页码（注意：第一页是从0开始）
        int pageSize = jsonObject.getInteger("pageSize"); //分页大小
        String sortProperty = jsonObject.getString("sortProperty");

        return forumService.getAll(pageIndex,pageSize,sortProperty);
    }

    @PostMapping("/totalNum")
    public ResponseUtils getAll() {

        return forumService.getForumNum();

    }

    //根据帖子id查询帖信息
    @PostMapping("/findById")
    public ResponseUtils findById(@RequestBody JSONObject jsonObject){

        Long forumId = jsonObject.getLong("forumId");
        return forumService.findById(forumId);
    }

}
