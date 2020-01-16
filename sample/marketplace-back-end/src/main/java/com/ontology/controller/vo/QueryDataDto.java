package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class QueryDataDto {
    @ApiModelProperty(name="pageNumber",value = "pageNumber",required = true)
    @NotNull
    private Integer pageNumber;

    @ApiModelProperty(name="pageSize",value = "pageSize",required = true)
    @NotNull
    private Integer pageSize;

    @ApiModelProperty(name="tag",value = "tag")
    private String tag;
}
