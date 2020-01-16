package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_order")
@Data
public class Order {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String invokeId;
    private String txHash;
    private String orderId;
    private String tokenId;
    private String jwt;
    private String provider;
    private String demander;
    private String price;
    private String judger;
    private Integer state;
    private Date createTime;
}
