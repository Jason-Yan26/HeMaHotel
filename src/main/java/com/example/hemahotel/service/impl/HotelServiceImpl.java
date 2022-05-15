package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONArray;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
                jsonArray.add(jsonObject1);
            }
            jsonObject.put("hotels",jsonArray);

            return ResponseUtils.response(200, "酒店关键字搜索成功", jsonObject);
        }
        else
            return ResponseUtils.response(404, "酒店关键字搜索失败", jsonObject);
    }
}
