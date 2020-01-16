package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_product")
@Data
public class Product {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String invokeId;
    private String dataId;
    private String authId;
    private String ontid;
    private String name;
    private String description;
    private String price;
    private Integer amount;
    private String tag;
    private String judger;
    private Integer state;
    private Date createTime;
}
