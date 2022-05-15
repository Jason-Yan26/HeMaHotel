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


//    //创建索引和映射
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

    //添加数据
//    @Test
//    public void addData() {
//
//        for(int i = 7402;i < 8000;i++) {
//
//            Hotel hotel = hotelRepository.findById(new Long((long)i)).get();
//
//            List<String> suggestList = new ArrayList<>();
//            String name = hotel.getName();
//            suggestList.add(name); //可以把多个内容作为suggest的数据源
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
//        // 使用suggest进行标题联想
//        CompletionSuggestionBuilder suggest = SuggestBuilders.completionSuggestion("name")
//                //根据什么前缀来联想
//                .prefix("天津")
//                // 跳过重复过滤
//                .skipDuplicates(true)
//                // 匹配数量
//                .size(10);
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
//        suggestBuilder.addSuggestion("hotel-suggest",suggest);
//
//        //执行查询
////        SearchResponse suggestResp = elasticsearchRestTemplate.suggest(suggestBuilder, GoodsDoc.class);
//        SearchResponse suggestResp = elasticsearchRestTemplate.suggest(suggestBuilder, SearchHotel.class);
//
//        //拿到Suggest结果
//        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> orderSuggest = suggestResp
//                .getSuggest().getSuggestion("hotel-suggest");
//
//        // 处理返回结果
//        List<String> suggests = orderSuggest.getEntries().stream()
//                .map(x -> x.getOptions().stream()
//                        .map(y->y.getText().toString())
//                        .collect(Collectors.toList())).findFirst().get();
//
//        // 输出内容
//        for (String str : suggests) {
//            System.out.println("自动补全 = " + str);
//        }
//    }


//    public List<String> getSuggestions(String prefix) {
//        try {
//            // 1.准备Request
//            SearchRequest request = new SearchRequest("hotel");
//            // 2.准备DSL
//            request.source().suggest(new SuggestBuilder().addSuggestion(
//                    "suggestions",
//                    SuggestBuilders.completionSuggestion("name")
//                            .prefix(prefix)
//                            .skipDuplicates(true)
//                            .size(10)
//            ));
//            // 3.发起请求
//            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            // 4.解析结果
//            Suggest suggest = response.getSuggest();
//            // 4.1.根据补全查询名称，获取补全结果
//            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
//            // 4.2.获取options
//            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
//            // 4.3.遍历
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
//        String suggestField="name";//指定在哪个字段搜索
//        String suggestValue="王二";//输入的信息
//        Integer suggestMaxCount=10;//获得最大suggest条数
//
//        CompletionSuggestionBuilder suggestionBuilderDistrict = new CompletionSuggestionBuilder(suggestField).prefix(suggestValue).size(suggestMaxCount);
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
//        suggestBuilder.addSuggestion("student_suggest", suggestionBuilderDistrict);//添加suggest
//
//        //设置查询builder的index,type,以及建议
//        SearchRequestBuilder requestBuilder = this.elasticsearchRestTemplate.getClient().prepareSearch("student_index").setTypes("student").suggest(suggestBuilder);
//        System.out.println(requestBuilder.toString());
//
//        SearchResponse response = requestBuilder.get();
//        Suggest suggest = response.getSuggest();//suggest实体
//
//        Set<String> suggestSet = new HashSet<>();//set
//        int maxSuggest = 0;
//        if (suggest != null) {
//            Suggest.Suggestion result = suggest.getSuggestion("student_suggest");//获取suggest,name任意string
//            for (Object term : result.getEntries()) {
//
//                if (term instanceof CompletionSuggestion.Entry) {
//                    CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
//                    if (!item.getOptions().isEmpty()) {
//                        //若item的option不为空,循环遍历
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
//    //创建索引
//    @Test
//    void createIndex() {
//        //创建索引，系统初始化会自动创建索引
//        System.out.println("创建索引");
//    }
//
//    //删除索引
//    @Test
//    void deleteIndex() {
//        boolean delete = elasticsearchRestTemplate.indexOps(SearchHotel.class).delete();
//        System.out.println("删除索引：" + delete);
//    }
//
//    //新增文档
//    @Test
//    void save() {
//        SearchHotel  searchHotel= new SearchHotel();
//        searchHotel.setId(1L);
//        searchHotel.setName("北京丽景湾国际酒店");
//        searchHotel.setLocation("北京东四环十里堡北里28号");
//        searchHotel.setStar(5);
//        searchHotel.setPicture("https://img14.360buyimg.com/hotel/jfs/t1/172321/37/10598/775776/60a629f4Ee9175b6b/00c800117e6a7381.png");
//        SearchHotel save = searchHotelRepository.save(searchHotel);
//    }


//    @Test
//    public void fuzzySearch(){
//
//        int page = 0;
//        int pageNum = 10;
//        String searchKeyWord = "北京";
//        int lowerStar = 1;
//        int upperStar = 5;
//
//        // 1. Create query on multiple fields enabling fuzzy search
//        Query searchQuery;
//
////      //对商品名，商品详情， 商品id赋予不同的权值
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
//                        //酒店星级匹配
//                        .must(QueryBuilders.rangeQuery("star").from(lowerStar).to(upperStar));
//
//        searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(functionScoreQueryBuilder)
//                //筛选条件匹配
//                .withFilter(boolQueryBuilder)
//                //分页匹配
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
//        //如果得到的列表为空， 抛出异常
//        if (hotelMatches.size() == 0){
//            System.out.println("查找结果为空！");
//        }
//        System.out.println("查找成功！");
//
//        JSONObject jsonObject = new JSONObject();
//
//
//
////        return ResponseUtils.success("查找成功", jsonArray);
//    }

}
