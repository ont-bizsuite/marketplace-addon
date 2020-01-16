package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AuthDto {

    @ApiModelProperty(name="商品id",value = "商品id",required = true)
    @NotNull
    private Integer id;

    @ApiModelProperty(name="dataId",value = "dataId",required = true)
    @NotBlank
    private String dataId;

    @ApiModelProperty(name="symbol",value = "symbol",required = true)
    @NotBlank
    private String symbol;

    @ApiModelProperty(name="name",value = "name",required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(name="description",value = "description")
    private String description;

    @ApiModelProperty(name="tags",value = "tags",required = true)
    @NotEmpty
    private List<String> tags;

    @ApiModelProperty(name="amount",value = "上架授权数量",required = true)
    @NotNull
    private Integer amount;

    @ApiModelProperty(name="price",value = "单价",required = true)
    @NotNull
    private Long price;

    @ApiModelProperty(name="transferCount",value = "token允许流转次数",required = true)
    @NotNull
    private Integer transferCount;

    @ApiModelProperty(name="accessCount",value = "token允许访问次数",required = true)
    @NotNull
    private Integer accessCount;

    @ApiModelProperty(name="expireTime",value = "token的过期时间",required = true)
    @NotNull
    private Long expireTime;

    @ApiModelProperty(name="provider",value = "提供方ontid",required = true)
    @NotBlank
    private String provider;


}
