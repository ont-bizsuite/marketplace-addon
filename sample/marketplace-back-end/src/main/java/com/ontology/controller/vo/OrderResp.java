package com.ontology.controller.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ontology.utils.DateSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class OrderResp {
    private Integer id;

    private String txHash;
    private String orderId;
    private String tokenId;
    private String jwt;
    private String provider;
    private String demander;
    private String price;
    private String judger;
    private Integer state;
    private Boolean isExpire = false;
    @JsonSerialize(using = DateSerializer.class)
    private Date createTime;
}
