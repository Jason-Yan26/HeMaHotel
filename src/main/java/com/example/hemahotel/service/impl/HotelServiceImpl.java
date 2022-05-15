package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.CommentRepository;
import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.elasticSearch.SearchHotel;
import com.example.hemahotel.entity.Comment;
import com.example.hemahotel.entity.Hotel;
import com.example.hemahotel.entity.User;
import com.example.hemahotel.service.HotelService;
import com.example.hemahotel.utils.ResponseUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


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

    /** 酒店名称自动补全*/
    public ResponseUtils hotelNameCompletion(String prefix){
        JSONObject jsonObject = new JSONObject();

        // 使用suggest进行标题联想
        CompletionSuggestionBuilder suggest = SuggestBuilders.completionSuggestion("name")
                //根据什么前缀来联想
                .prefix(prefix)
                // 跳过重复过滤
                .skipDuplicates(true)
                // 匹配数量
                .size(10);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("hotel-suggest",suggest);

        //执行查询
        SearchResponse suggestResp = elasticsearchRestTemplate.suggest(suggestBuilder, SearchHotel.class);

        //拿到Suggest结果
        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> orderSuggest = suggestResp
                .getSuggest().getSuggestion("hotel-suggest");

        // 处理返回结果
        List<String> suggests = orderSuggest.getEntries().stream()
                .map(x -> x.getOptions().stream()
                        .map(y->y.getText().toString())
                        .collect(Collectors.toList())).findFirst().get();
        if(suggests.size() != 0){

            jsonObject.put("names",suggests);
            return ResponseUtils.response(200, "酒店名称搜索自动补全成功", jsonObject);
        }
        else{
            return ResponseUtils.response(404, "酒店名称搜索不到相关补全建议", jsonObject);
        }
    }
}
