package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ArbitrationDto {
    @ApiModelProperty(name="orderId",value = "orderId",required = true)
    @NotBlank
    private String orderId;

    @ApiModelProperty(name="isWin",value = "仲裁结果，买家胜利为true",required = true)
    @NotNull
    private Boolean isWin;

    @ApiModelProperty(name="makerReceiveAmount",value = "卖家获取钱款",required = true)
    @NotNull
    private Long makerReceiveAmount;

    @ApiModelProperty(name="takerReceiveAmount",value = "买家获取钱款",required = true)
    @NotNull
    private Long takerReceiveAmount;
}
