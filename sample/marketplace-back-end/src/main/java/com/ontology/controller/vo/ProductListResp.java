package com.ontology.controller.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ontology.utils.Constant;
import com.ontology.utils.DateSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductListResp {
    private Integer id;

    private String name;
    private List<String> tags;
    private String coin = Constant.COIN_ONG;
    private String price;
    @JsonSerialize(using = DateSerializer.class)
    private Date date;
}
