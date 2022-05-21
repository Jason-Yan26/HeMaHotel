package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.ForumRepository;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.entity.Forum;
import com.example.hemahotel.entity.User;
import com.example.hemahotel.service.ForumService;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@Slf4j
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private UserRepository userRepository;

    //发帖
    public ResponseUtils add(Long userId, String title, String content){

        JSONObject jsonObject = new JSONObject();
        Optional<User> u = userRepository.findById(userId);

        if(!u.isPresent()){
            jsonObject.put("id",userId);
            return ResponseUtils.response(400, "用户不存在", jsonObject);
        }
        //用户存在
        else {
            String username = u.get().getUsername();
            String avatar = u.get().getAvatar();
            forumRepository.save(new Forum(userId, username, avatar, title, content, new Timestamp(System.currentTimeMillis())));
            return ResponseUtils.response(200, "发帖成功", jsonObject);
        }
    }

    // 返回所有帖子
    public ResponseUtils getAll(Integer pageIndex, Integer pageSize, String sortProperty) {

        // 分页排序
        Sort sort = Sort.by(Sort.Order.desc(sortProperty)); // sortProperty:排序属性
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<Forum> forums = forumRepository.findAll(pageable);
        JSONObject res = new JSONObject();
        res.put("forum", forums.getContent());
        return ResponseUtils.response(200, "论坛获取成功", res);
    }

    // 按照id找相关帖子
    public ResponseUtils findById(Long forumId) {

        JSONObject jsonObject = new JSONObject();
        Optional<Forum> f = forumRepository.findById(forumId);

        if (!f.isPresent()){
            jsonObject.put("id",forumId);
            return ResponseUtils.response(400, "找不到id所对应的相关帖子", jsonObject);
        }
        else{

            Forum forum = f.get();
            jsonObject.put("forumId", forumId);
            jsonObject.put("title", forum.getTitle());
            jsonObject.put("content", forum.getContent());
            //jsonObject.put("username", forum.getUsername());
            //jsonObject.put("userAvatar", forum.getUserAvatar());

            return ResponseUtils.success("查找帖子成功", jsonObject);
        }
    }

    @Override
    public ResponseUtils getForumNum() {

        Long number = forumRepository.count();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("forumNum",number);
        return ResponseUtils.response(200, "论坛帖子总数获取成功", jsonObject);

    }
}
