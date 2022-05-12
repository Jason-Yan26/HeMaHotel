package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.CommentRepository;
import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.entity.Comment;
import com.example.hemahotel.entity.Hotel;
import com.example.hemahotel.entity.User;
import com.example.hemahotel.service.HotelService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public ResponseUtils CreateComment(Long userId, String comment, Long hotelId, int star) {

        JSONObject jsonObject = new JSONObject();
        Optional<Hotel> h = hotelRepository.findById(hotelId);

        if(!h.isPresent()){
            return ResponseUtils.response(401, "评论的酒店不存在，请重新提交评论", jsonObject);
        }
        else {
            Comment comments = Comment.builder()
                    .content(comment).userId(userId).hotelId(hotelId)
                    .createTime(new Timestamp(System.currentTimeMillis()))
                    .star(star).build();
            try{
                commentRepository.save(comments);
                return ResponseUtils.success("保存评论成功", null);
            }catch (Exception e){
                return ResponseUtils.response(400, "评论失败", jsonObject);
            }
        }
    }

    @Override
    public ResponseUtils findCommentByHotelId(Long hotelId) {

        List<Comment> comments = commentRepository.findByHotelId(hotelId);

        if(!comments.isEmpty()){
            return ResponseUtils.success("查找成功", comments);
        }
        else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hotelId",hotelId);
            return ResponseUtils.response(400, "找不到相关评论", jsonObject);
        }
    }
}
