package com.example.hemahotel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.elasticSearch.SearchHotel;
import com.example.hemahotel.elasticSearch.SearchHotelRepository;
import com.example.hemahotel.entity.Hotel;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class HeMaHotelApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private SearchHotelRepository searchHotelRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;


//    //?????????????????????
//    @Test
//    public void testCreateIndex(){
//        elasticsearchRestTemplate.p
//    }

//    @Test
//    public void testInsert(){
//        Hotel hotel =  hotelRepository.findById(7401L).get();
//
//        SearchHotel s = SearchHotel.builder()
//                    .id(hotel.getId())
//                    .name(hotel.getName())
//                    .location(hotel.getLocation())
//                    .star(hotel.getStar())
//                    .description(hotel.getDescription())
//                    .build();
//
//        searchHotelRepository.save(s);
//    }

    //????????????
//    @Test
//    public void addData() {
//
//        for(int i = 7402;i < 8000;i++) {
//
//            Hotel hotel = hotelRepository.findById(new Long((long)i)).get();
//
//            List<String> suggestList = new ArrayList<>();
//            String name = hotel.getName();
//            suggestList.add(name); //???????????????????????????suggest????????????
//            Completion suggest = new Completion(suggestList.toArray(new String[suggestList.size()]));
//            SearchHotel s = new SearchHotel(hotel.getId(), suggest, hotel.getLocation(), hotel.getStar(), hotel.getDescription());
//            searchHotelRepository.save(s);
//        }
//
//    }

//    @Test
//    public void testDelete() {
//        searchHotelRepository.deleteAll();
//    }

//    @Test
//    public void search(){
//        // ??????suggest??????????????????
//        CompletionSuggestionBuilder suggest = SuggestBuilders.completionSuggestion("name")
//                //???????????????????????????
//                .prefix("??????")
//                // ??????????????????
//                .skipDuplicates(true)
//                // ????????????
//                .size(10);
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
//        suggestBuilder.addSuggestion("hotel-suggest",suggest);
//
//        //????????????
////        SearchResponse suggestResp = elasticsearchRestTemplate.suggest(suggestBuilder, GoodsDoc.class);
//        SearchResponse suggestResp = elasticsearchRestTemplate.suggest(suggestBuilder, SearchHotel.class);
//
//        //??????Suggest??????
//        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> orderSuggest = suggestResp
//                .getSuggest().getSuggestion("hotel-suggest");
//
//        // ??????????????????
//        List<String> suggests = orderSuggest.getEntries().stream()
//                .map(x -> x.getOptions().stream()
//                        .map(y->y.getText().toString())
//                        .collect(Collectors.toList())).findFirst().get();
//
//        // ????????????
//        for (String str : suggests) {
//            System.out.println("???????????? = " + str);
//        }
//    }


//    public List<String> getSuggestions(String prefix) {
//        try {
//            // 1.??????Request
//            SearchRequest request = new SearchRequest("hotel");
//            // 2.??????DSL
//            request.source().suggest(new SuggestBuilder().addSuggestion(
//                    "suggestions",
//                    SuggestBuilders.completionSuggestion("name")
//                            .prefix(prefix)
//                            .skipDuplicates(true)
//                            .size(10)
//            ));
//            // 3.????????????
//            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            // 4.????????????
//            Suggest suggest = response.getSuggest();
//            // 4.1.?????????????????????????????????????????????
//            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
//            // 4.2.??????options
//            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
//            // 4.3.??????
//            List<String> list = new ArrayList<>(options.size());
//            for (CompletionSuggestion.Entry.Option option : options) {
//                String text = option.getText().toString();
//                list.add(text);
//            }
//            return list;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


//    @Test
//    public void testSuggestCompletionProc() {
//
//        String suggestField="name";//???????????????????????????
//        String suggestValue="??????";//???????????????
//        Integer suggestMaxCount=10;//????????????suggest??????
//
//        CompletionSuggestionBuilder suggestionBuilderDistrict = new CompletionSuggestionBuilder(suggestField).prefix(suggestValue).size(suggestMaxCount);
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
//        suggestBuilder.addSuggestion("student_suggest", suggestionBuilderDistrict);//??????suggest
//
//        //????????????builder???index,type,????????????
//        SearchRequestBuilder requestBuilder = this.elasticsearchRestTemplate.getClient().prepareSearch("student_index").setTypes("student").suggest(suggestBuilder);
//        System.out.println(requestBuilder.toString());
//
//        SearchResponse response = requestBuilder.get();
//        Suggest suggest = response.getSuggest();//suggest??????
//
//        Set<String> suggestSet = new HashSet<>();//set
//        int maxSuggest = 0;
//        if (suggest != null) {
//            Suggest.Suggestion result = suggest.getSuggestion("student_suggest");//??????suggest,name??????string
//            for (Object term : result.getEntries()) {
//
//                if (term instanceof CompletionSuggestion.Entry) {
//                    CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
//                    if (!item.getOptions().isEmpty()) {
//                        //???item???option?????????,????????????
//                        for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
//                            String tip = option.getText().toString();
//                            if (!suggestSet.contains(tip)) {
//                                suggestSet.add(tip);
//                                ++maxSuggest;
//                            }
//                        }
//                    }
//                }
//                if (maxSuggest >= suggestMaxCount) {
//                    break;
//                }
//            }
//        }
//
//        List<String> suggests = Arrays.asList(suggestSet.toArray(new String[]{}));
//
//        suggests.forEach((s)->{
//            System.out.println(s);
//        });
//
////		return	 suggests;
//
//    }


//
//    //????????????
//    @Test
//    void createIndex() {
//        //???????????????????????????????????????????????????
//        System.out.println("????????????");
//    }
//
//    //????????????
//    @Test
//    void deleteIndex() {
//        boolean delete = elasticsearchRestTemplate.indexOps(SearchHotel.class).delete();
//        System.out.println("???????????????" + delete);
//    }
//
//    //????????????
//    @Test
//    void save() {
//        SearchHotel  searchHotel= new SearchHotel();
//        searchHotel.setId(1L);
//        searchHotel.setName("???????????????????????????");
//        searchHotel.setLocation("??????????????????????????????28???");
//        searchHotel.setStar(5);
//        searchHotel.setPicture("https://img14.360buyimg.com/hotel/jfs/t1/172321/37/10598/775776/60a629f4Ee9175b6b/00c800117e6a7381.png");
//        SearchHotel save = searchHotelRepository.save(searchHotel);
//    }


//    @Test
//    public void fuzzySearch(){
//
//        int page = 0;
//        int pageNum = 10;
//        String searchKeyWord = "??????";
//        int lowerStar = 1;
//        int upperStar = 5;
//
//        // 1. Create query on multiple fields enabling fuzzy search
//        Query searchQuery;
//
////      //?????????????????????????????? ??????id?????????????????????
//        List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders = new ArrayList<>();
//
////        filterFunctionBuilders.add(
////                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
////                        QueryBuilders.termQuery("name", searchKeyWord), ScoreFunctionBuilders.weightFactorFunction(50)));
//
//        filterFunctionBuilders.add(
//                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
//                        QueryBuilders.matchPhraseQuery("location", searchKeyWord), ScoreFunctionBuilders.weightFactorFunction(50)));
//
//        //Combine
//        FunctionScoreQueryBuilder.FilterFunctionBuilder[] builders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[filterFunctionBuilders.size()];
//        filterFunctionBuilders.toArray(builders);
//        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(builders)
//                .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
//                .setMinScore(2);
//
//        BoolQueryBuilder boolQueryBuilder =
//                QueryBuilders.boolQuery()
//                        //??????????????????
//                        .must(QueryBuilders.rangeQuery("star").from(lowerStar).to(upperStar));
//
//        searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(functionScoreQueryBuilder)
//                //??????????????????
//                .withFilter(boolQueryBuilder)
//                //????????????
//                .withPageable(PageRequest.of(page, pageNum))
//                .build();
//
//        // 2. Execute search
//        SearchHits<SearchHotel> hotelHits =
//                elasticsearchOperations.search(searchQuery, SearchHotel.class,IndexCoordinates.of("hotel"));
//
//
//        // 3. Map searchHits to product list
//        List<SearchHotel> hotelMatches = new ArrayList<SearchHotel>();
//        hotelHits.forEach(searchHit -> {
//            hotelMatches.add(searchHit.getContent());
//        });
//
//        //?????????????????????????????? ????????????
//        if (hotelMatches.size() == 0){
//            System.out.println("?????????????????????");
//        }
//        System.out.println("???????????????");
//
//        JSONObject jsonObject = new JSONObject();
//
//
//
////        return ResponseUtils.success("????????????", jsonArray);
//    }

}
