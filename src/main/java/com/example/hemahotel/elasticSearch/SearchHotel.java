package com.example.hemahotel.elasticSearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(indexName = "hotel")
public class SearchHotel {

    @Id
    private Long id;//酒店ID

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String name;//酒店名称

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String location;//酒店位置

//    @Field(type = FieldType.Keyword,index = false)
//    private String picture;//酒店图片地址

    @Field(type = FieldType.Integer)
    private Integer star;//酒店星级

    @Field(type = FieldType.Text)
    private String description;//酒店描述

    @CompletionField(analyzer = "ik_max_word",searchAnalyzer = "ik_smart",maxInputLength = 100)
    private Completion suggestion;//酒店搜索补全建议

//    @Field(type = FieldType.Keyword,index = false)
//    private String phone;//酒店联系电话

//    @Field(type = FieldType.Date, format = DateFormat.basic_date_time)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd",timezone="GMT+8")
//    private Date createTime;//创建时间
//
//    @Field(type = FieldType.Date,format = DateFormat.basic_date_time)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd",timezone="GMT+8")
//    private Date updateTime;//更改时间
}
