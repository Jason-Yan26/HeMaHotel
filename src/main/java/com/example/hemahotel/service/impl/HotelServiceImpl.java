package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.CommentRepository;
import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.dao.RoomCategoryRepository;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.elasticSearch.SearchHotel;
import com.example.hemahotel.elasticSearch.SearchHotelRepository;
import com.example.hemahotel.entity.*;
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
import org.springframework.data.elasticsearch.core.suggest.Completion;
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

    @Autowired
    private RoomCategoryRepository roomCategoryRepository;


    @Autowired
    private SearchHotelRepository searchHotelRepository;

    public ResponseUtils createComment(Long userId, String comment, Long hotelId, int star) {

        JSONObject jsonObject = new JSONObject();
        Optional<Hotel> h = hotelRepository.findById(hotelId);

        if(!h.isPresent()){
            return ResponseUtils.response(401, "????????????????????????????????????????????????", jsonObject);
        }
        else {
            Comment comments = Comment.builder()
                    .content(comment).userId(userId).hotelId(hotelId)
                    .createTime(new Timestamp(System.currentTimeMillis()))
                    .star(star).build();
            try{
                commentRepository.save(comments);
                return ResponseUtils.success("??????????????????", null);
            }catch (Exception e){
                return ResponseUtils.response(400, "????????????", jsonObject);
            }
        }
    }


    public ResponseUtils findCommentByHotelId(Long hotelId,int pageIndex,int pageSize) {

        Sort sort = Sort.by(Sort.Order.desc("createTime")); // property:????????????
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

            jsonObject1.put("userName",userRepository.findById(comment.getUserId()).get().getUsername());
            jsonObjects.add(jsonObject1);
        }

        if(!comments.isEmpty()){
            return ResponseUtils.success("??????????????????", jsonObjects);
        }
        else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hotelId",hotelId);
            return ResponseUtils.response(400, "?????????????????????", jsonObject);
        }
    }

    @Override
    public ResponseUtils getCommentNumByHotelId(Long hotelId) {

        Long number = commentRepository.countByHotelId(hotelId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("commentNum",number);
        return ResponseUtils.response(200, "??????????????????????????????", jsonObject);

    }

    /** ????????????????????????*/
    public ResponseUtils hotelNameCompletion(String prefix){
        JSONObject jsonObject = new JSONObject();

        // ??????suggest??????????????????
        CompletionSuggestionBuilder suggest = SuggestBuilders.completionSuggestion("suggestion")
                //???????????????????????????
                .prefix(prefix)
                // ??????????????????
                .skipDuplicates(true)
                // ????????????
                .size(10);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("hotel-suggest",suggest);

        //????????????
        SearchResponse suggestResp = elasticsearchRestTemplate.suggest(suggestBuilder, SearchHotel.class);

        //??????Suggest??????
        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> orderSuggest = suggestResp
                .getSuggest().getSuggestion("hotel-suggest");

        // ??????????????????
        List<String> suggests = orderSuggest.getEntries().stream()
                .map(x -> x.getOptions().stream()
                        .map(y->y.getText().toString())
                        .collect(Collectors.toList())).findFirst().get();
        if(suggests.size() != 0){

            jsonObject.put("suggestions",suggests);
            return ResponseUtils.response(200, "??????????????????????????????", jsonObject);
        }
        else{
            return ResponseUtils.response(404, "???????????????????????????", jsonObject);
        }
    }


    /**?????????????????????*/
    public ResponseUtils searchHotelByKeyword(String searchKeyWord,int page,int pageNum,int lowerStar,int upperStar){
        JSONObject jsonObject = new JSONObject();

        // 1. Create query on multiple fields enabling fuzzy search
        Query searchQuery;

//      //????????????????????????????????????????????????
        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders = new ArrayList<>();

        //?????????
        filterFunctionBuilders.add(
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.matchQuery("name", searchKeyWord), ScoreFunctionBuilders.weightFactorFunction(80)));
        //??????
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
                        //??????????????????
                        .must(QueryBuilders.rangeQuery("star").from(lowerStar).to(upperStar));

        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(functionScoreQueryBuilder)
                //??????????????????
                .withFilter(boolQueryBuilder)
                //????????????
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


//        //?????????????????????????????????????????????
//        Query searchQueryTotal = new NativeSearchQueryBuilder()
//                .withQuery(functionScoreQueryBuilder)
//                //??????????????????
//                .withFilter(boolQueryBuilder)
//                .build();
//
//        SearchHits<SearchHotel> hotelHitsTotal =
//                elasticsearchOperations.search(searchQuery, SearchHotel.class, IndexCoordinates.of("hotel"));
//
//        Long totalNumber = hotelHitsTotal.getTotalHits();

        //?????????????????????????????? ????????????
        if (hotelMatches.size() != 0){
            JSONArray jsonArray = new JSONArray();
            for(SearchHotel hotel:hotelMatches){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id",hotel.getId());
                jsonObject1.put("name",hotel.getName());
                jsonObject1.put("location",hotel.getLocation());
                jsonObject1.put("star",hotel.getStar());
                jsonObject1.put("description",hotel.getDescription());

                //??????es???????????????????????????,??????id????????????????????????
                Long hotelId = hotel.getId();
                String picture = hotelRepository.findById(hotelId).get().getPicture();
                jsonObject1.put("picture",picture);
                //??????????????????
                List<RoomCategory> roomCategories = roomCategoryRepository.findByHotelId(hotel.getId());
                Double min_price = 99999.0;
                for(RoomCategory roomCategory:roomCategories){
                    if(roomCategory.getPrice() < min_price)
                        min_price = roomCategory.getPrice();
                }
                jsonObject1.put("price",min_price);

                jsonArray.add(jsonObject1);
            }
            jsonObject.put("hotels",jsonArray);
//            jsonObject.put("hotels",hotelMatches);
            jsonObject.put("totalNumber",hotelHits.getTotalHits());

            return ResponseUtils.response(200, "???????????????????????????", jsonObject);
        }
        else
            return ResponseUtils.response(404, "???????????????????????????", jsonObject);
    }


    /** ??????????????????*/
    public ResponseUtils getHotelInformation(Long hotelId){
        JSONObject jsonObject = new JSONObject();
        Optional<Hotel> h = hotelRepository.findById(hotelId);

        if(h.isPresent()){
            Hotel hotel = h.get();
            jsonObject.put("id",hotel.getId());
            jsonObject.put("location",hotel.getLocation());
            jsonObject.put("star",hotel.getStar());
            jsonObject.put("name",hotel.getName());
            jsonObject.put("description",hotel.getDescription());
            jsonObject.put("picture",hotel.getPicture());
            jsonObject.put("phone",hotel.getPhone());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            jsonObject.put("createTime",simpleDateFormat.format(hotel.getCreateTime()));

            List<Comment> comments = commentRepository.findByHotelId(hotel.getId());

            double avgScore = 0;
            for(Comment comment:comments){
                avgScore += comment.getStar();
            }
            if(comments.size() > 0 )
                avgScore = avgScore/comments.size();
            else
                avgScore = -1;

            jsonObject.put("avgScore",avgScore);

            return ResponseUtils.response(200, "????????????????????????", jsonObject);
        }

        return ResponseUtils.response(404, "????????????????????????", jsonObject);
    }

    /** ????????????????????????*/
    public ResponseUtils getHotelRecommendation(Long num){
        JSONObject jsonObject = new JSONObject();

        BoolQueryBuilder boolQueryBuilder =
                QueryBuilders.boolQuery()
                        //??????????????????
                        .must(QueryBuilders.rangeQuery("star").from(5).to(5));

        Query searchQuery;

        searchQuery = new NativeSearchQueryBuilder()
                //??????????????????
                .withFilter(boolQueryBuilder)
                //????????????
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

        //?????????????????????????????? ????????????
        if (hotelMatches.size() != 0){
            JSONArray jsonArray = new JSONArray();
            for(SearchHotel hotel:hotelMatches){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id",hotel.getId());
                jsonObject1.put("name",hotel.getName());
                jsonObject1.put("location",hotel.getLocation());
                jsonObject1.put("star",hotel.getStar());
                jsonObject1.put("description",hotel.getDescription());

                //??????es???????????????????????????,??????id????????????????????????
                Long hotelId = hotel.getId();
                String picture = hotelRepository.findById(hotelId).get().getPicture();
                jsonObject1.put("picture",picture);

                //??????????????????
                List<RoomCategory> roomCategories = roomCategoryRepository.findByHotelId(hotel.getId());
                Double min_price = 99999.0;
                for(RoomCategory roomCategory:roomCategories){
                    if(roomCategory.getPrice() < min_price)
                        min_price = roomCategory.getPrice();
                }
                jsonObject1.put("price",min_price);


                jsonArray.add(jsonObject1);
            }
            jsonObject.put("hotels",jsonArray);

            return ResponseUtils.response(200, "??????????????????", jsonObject);
        }
        else
            return ResponseUtils.response(404, "??????????????????", jsonObject);

    }


    /** ????????????*/
    public ResponseUtils addHotel(Long userId,String hotelName,String hotelLocation,String hotelPicture,Integer hotelStar,String hotelPhone,String hotelDescription){

        JSONObject jsonObject = new JSONObject();

        User user = userRepository.findById(userId).get();
        //??????????????????????????????????????????????????????????????????
        if(user.getIdentity() == 1){

            Timestamp createTime = new Timestamp(System.currentTimeMillis());//????????????
            Timestamp updateTime = new Timestamp(System.currentTimeMillis());//????????????

            Hotel hotel = new Hotel(hotelName,hotelLocation,hotelPicture,hotelStar,hotelPhone,hotelDescription,createTime,updateTime);
            hotel = hotelRepository.save(hotel);//???????????????MySQL?????????


            List<String> suggestList = new ArrayList<>();
            suggestList.add(hotel.getName()); //???????????????????????????suggest????????????
            Completion suggest = new Completion(suggestList.toArray(new String[suggestList.size()]));

            SearchHotel searchHotel = new SearchHotel(hotel.getId(),hotelName,hotelLocation,hotelStar,hotelDescription,suggest);

            searchHotelRepository.save(searchHotel);//???????????????ES?????????


            return ResponseUtils.response(200, "??????????????????", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "????????????????????????????????????", jsonObject);
        }
    }

    /** ????????????*/
    public ResponseUtils deleteHotel(Long userId,Long hotelId){
        JSONObject jsonObject = new JSONObject();

        User user = userRepository.findById(userId).get();
        //??????????????????????????????????????????????????????????????????
        if(user.getIdentity() == 1) {
            hotelRepository.deleteById(hotelId);
            searchHotelRepository.deleteById(hotelId);
            return ResponseUtils.response(200, "??????????????????", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "????????????????????????????????????", jsonObject);
        }
    }

}
