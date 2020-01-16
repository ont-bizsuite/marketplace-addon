package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OrderDto {
    @ApiModelProperty(name="id",value = "订单id",required = true)
    @NotBlank
    private String orderId;

}
