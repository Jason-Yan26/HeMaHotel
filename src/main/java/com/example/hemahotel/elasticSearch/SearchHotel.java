//package com.example.hemahotel.elasticSearch;
//
//import lombok.*;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.DateFormat;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//
//import java.sql.Date;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//@Builder
//@Document(indexName = "hotel")
//public class SearchHotel {
//
//    @Id
//    private Long id;//酒店ID
//
//    @Field(type = FieldType.Text)
//    private String name;//酒店名称
//
//    @Field(type = FieldType.Text)
//    private String location;//酒店位置
//
//    @Field(type = FieldType.Keyword,index = false)
//    private String picture;//酒店图片地址
//
//    @Field(type = FieldType.Integer)
//    private Integer star;//酒店星级
//
//    @Field(type = FieldType.Date,format = DateFormat.basic_date_time)
//    private Date createTime;
//
//    @Field(type = FieldType.Date,format = DateFormat.basic_date_time)
//    private Date updateTime;
//}
