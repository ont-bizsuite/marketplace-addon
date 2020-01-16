package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
public class DataIdDto {
    @ApiModelProperty(name="ontid",value = "ontid",required = true)
    @NotBlank
    private String ontid;

    @ApiModelProperty(name="file",value = "file",required = true)
    private MultipartFile file;
}
