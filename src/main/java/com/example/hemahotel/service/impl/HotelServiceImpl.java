package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.CommentRepository;
import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.elasticSearch.SearchHotel;
import com.example.hemahotel.entity.Comment;
import com.example.hemahotel.entity.Hotel;
import com.example.hemahotel.service.HotelService;
import com.example.hemahotel.utils.ResponseUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private UserRepository userRepository;


    @Override
    public ResponseUtils createComment(Long userId, String comment, Long hotelId, int star) {

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
    public ResponseUtils findCommentByHotelId(Long hotelId,int pageIndex,int pageSize) {

        Sort sort = Sort.by(Sort.Order.desc("createTime")); // property:排序属性
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<Comment> comments = commentRepository.findByHotelId(hotelId,pageable);

        List<JSONObject> jsonObjects = new ArrayList<>();
        for(Comment comment:comments.getContent()){
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id",comment.getId());
            jsonObject1.put("hotelId",comment.getHotelId());
            jsonObject1.put("content",comment.getContent());
            jsonObject1.put("star",comment.getStar());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            jsonObject1.put("createTime",sdf.format(comment.getCreateTime()));
            jsonObject1.put("userId",comment.getUserId());
            System.out.println(comment.getUserId());
            jsonObject1.put("userName",userRepository.findById(comment.getUserId()).get().getUsername());
            jsonObjects.add(jsonObject1);
        }

        if(!comments.isEmpty()){
            return ResponseUtils.success("评论查找成功", jsonObjects);
        }
        else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hotelId",hotelId);
            return ResponseUtils.response(400, "找不到相关评论", jsonObject);
        }
    }

    @Override
    public ResponseUtils getCommentNumByHotelId(Long hotelId) {

        Long number = commentRepository.countByHotelId(hotelId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("commentNum",number);
        return ResponseUtils.response(200, "酒店评论总数获取成功", jsonObject);

    }

    /** 酒店搜索自动补全*/
    public ResponseUtils hotelNameCompletion(String prefix){
        JSONObject jsonObject = new JSONObject();

        // 使用suggest进行标题联想
        CompletionSuggestionBuilder suggest = SuggestBuilders.completionSuggestion("suggestion")
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

            jsonObject.put("suggestions",suggests);
            return ResponseUtils.response(200, "酒店搜索自动补全成功", jsonObject);
        }
        else{
            return ResponseUtils.response(404, "酒店搜索无补全建议", jsonObject);
        }
    }


    /**酒店关键字搜索*/
    public ResponseUtils searchHotelByKeyword(String searchKeyWord,int page,int pageNum,int lowerStar,int upperStar){
        JSONObject jsonObject = new JSONObject();

        // 1. Create query on multiple fields enabling fuzzy search
        Query searchQuery;

//      //对酒店名、酒店位置赋予不同的权值
        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders = new ArrayList<>();

        //不分词
        filterFunctionBuilders.add(
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.matchQuery("name", searchKeyWord), ScoreFunctionBuilders.weightFactorFunction(80)));
        //分词
        filterFunctionBuilders.add(
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.matchPhraseQuery("name", searchKeyWord), ScoreFunctionBuilders.weightFactorFunction(80)));
        filterFunctionBuilders.add(
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.matchQuery("location", searchKeyWord), ScoreFunctionBuilders.weightFactorFunction(40)));

        //Combine
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] builders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[filterFunctionBuilders.size()];
        filterFunctionBuilders.toArray(builders);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(builders)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
                .setMinScore(2);

        BoolQueryBuilder boolQueryBuilder =
                QueryBuilders.boolQuery()
                        //酒店星级匹配
                        .must(QueryBuilders.rangeQuery("star").from(lowerStar).to(upperStar));

        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(functionScoreQueryBuilder)
                //筛选条件匹配
                .withFilter(boolQueryBuilder)
                //分页匹配
                .withPageable(PageRequest.of(page, pageNum))
                .build();

        // 2. Execute search
        SearchHits<SearchHotel> hotelHits =
                elasticsearchOperations.search(searchQuery, SearchHotel.class, IndexCoordinates.of("hotel"));

        // 3. Map searchHits to product list
        List<SearchHotel> hotelMatches = new ArrayList<SearchHotel>();
        hotelHits.forEach(searchHit -> {
            hotelMatches.add(searchHit.getContent());
        });


//        //不分页，获取当前查询的记录总数
//        Query searchQueryTotal = new NativeSearchQueryBuilder()
//                .withQuery(functionScoreQueryBuilder)
//                //筛选条件匹配
//                .withFilter(boolQueryBuilder)
//                .build();
//
//        SearchHits<SearchHotel> hotelHitsTotal =
//                elasticsearchOperations.search(searchQuery, SearchHotel.class, IndexCoordinates.of("hotel"));
//
//        Long totalNumber = hotelHitsTotal.getTotalHits();

        //如果得到的列表为空， 抛出异常
        if (hotelMatches.size() != 0){
            JSONArray jsonArray = new JSONArray();
            for(SearchHotel hotel:hotelMatches){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id",hotel.getId());
                jsonObject1.put("name",hotel.getName());
                jsonObject1.put("location",hotel.getLocation());
                jsonObject1.put("star",hotel.getStar());
                jsonObject1.put("description",hotel.getDescription());

                //由于es中没有存储酒店图片,根据id到数据库中去查找
                Long hotelId = hotel.getId();
                String picture = hotelRepository.findById(hotelId).get().getPicture();
                jsonObject1.put("picture",picture);

                jsonArray.add(jsonObject1);
            }
            jsonObject.put("hotels",jsonArray);
//            jsonObject.put("hotels",hotelMatches);
            jsonObject.put("totalNumber",hotelHits.getTotalHits());

            return ResponseUtils.response(200, "酒店关键字搜索成功", jsonObject);
        }
        else
            return ResponseUtils.response(404, "酒店关键字搜索失败", jsonObject);
    }


    /** 获取酒店信息*/
    public ResponseUtils getHotelInformation(Long hotelId){
        JSONObject jsonObject = new JSONObject();
        Optional<Hotel> h = hotelRepository.findById(hotelId);

        if(h.isPresent()){
            Hotel hotel = h.get();
            jsonObject.put("information",hotel);
            return ResponseUtils.response(200, "酒店信息获取成功", jsonObject);
        }

        return ResponseUtils.response(404, "酒店信息获取失败", jsonObject);
    }

    /** 获取推荐酒店信息*/
    public ResponseUtils getHotelRecommendation(Long num){
        JSONObject jsonObject = new JSONObject();

        BoolQueryBuilder boolQueryBuilder =
                QueryBuilders.boolQuery()
                        //酒店星级匹配
                        .must(QueryBuilders.rangeQuery("star").from(5).to(5));

        Query searchQuery;

        searchQuery = new NativeSearchQueryBuilder()
                //筛选条件匹配
                .withFilter(boolQueryBuilder)
                //分页匹配
                .withPageable(PageRequest.of(0, num.intValue()))
                .build();

        // 2. Execute search
        SearchHits<SearchHotel> hotelHits =
                elasticsearchOperations.search(searchQuery, SearchHotel.class, IndexCoordinates.of("hotel"));

        // 3. Map searchHits to product list
        List<SearchHotel> hotelMatches = new ArrayList<SearchHotel>();
        hotelHits.forEach(searchHit -> {
            hotelMatches.add(searchHit.getContent());
        });

        //如果得到的列表为空， 抛出异常
        if (hotelMatches.size() != 0){
            JSONArray jsonArray = new JSONArray();
            for(SearchHotel hotel:hotelMatches){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id",hotel.getId());
                jsonObject1.put("name",hotel.getName());
                jsonObject1.put("location",hotel.getLocation());
                jsonObject1.put("star",hotel.getStar());
                jsonObject1.put("description",hotel.getDescription());

                //由于es中没有存储酒店图片,根据id到数据库中去查找
                Long hotelId = hotel.getId();
                String picture = hotelRepository.findById(hotelId).get().getPicture();
                jsonObject1.put("picture",picture);

                jsonArray.add(jsonObject1);
            }
            jsonObject.put("hotels",jsonArray);

            return ResponseUtils.response(200, "酒店推荐成功", jsonObject);
        }
        else
            return ResponseUtils.response(404, "酒店推荐失败", jsonObject);

    }
}
