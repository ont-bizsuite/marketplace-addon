package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PurchaseDto {
    @ApiModelProperty(name="authId",value = "authId",required = true)
    @NotBlank
    private String authId;

    @ApiModelProperty(name="提供方ontid",value = "提供方ontid",required = true)
    @NotBlank
    private String provider;

    @ApiModelProperty(name="需求方ontid",value = "需求方ontid",required = true)
    @NotBlank
    private String demander;

    @ApiModelProperty(name="price",value = "单价",required = true)
    @NotNull
    private Long price;

    @ApiModelProperty(name="tokenAmount",value = "购买数量",required = true)
    @NotNull
    private Integer tokenAmount;
}
