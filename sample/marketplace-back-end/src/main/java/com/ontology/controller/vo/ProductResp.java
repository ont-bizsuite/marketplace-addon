package com.ontology.controller.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ontology.utils.Constant;
import com.ontology.utils.DateSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductResp {
    private Integer id;

    private String dataId;
    private String authId;
    private String ontid;
    private String name;
    private List<String> tags;
    private String coin = Constant.COIN_ONG;
    private String price;
    private Integer amount;
    private String description;
    private Integer state;
    @JsonSerialize(using = DateSerializer.class)
    private Date createTime;
}
