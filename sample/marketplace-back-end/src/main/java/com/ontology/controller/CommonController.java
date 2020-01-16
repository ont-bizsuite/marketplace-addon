package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageCallbackDto;
import com.ontology.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = "callback")
@RestController
@RequestMapping("/api/v1/back")
@CrossOrigin
public class CommonController {
    @Autowired
    private CommonService commonService;

    @ApiOperation(value = "get param", notes = "get param", httpMethod = "GET")
    @GetMapping("/params/{id}")
    public String getParams(@PathVariable String id) throws Exception {
        String action = "getDataIdParams";
        return commonService.callbackAndGetParams(action,id);
    }

    @ApiOperation(value = "query result", notes = "query result", httpMethod = "GET")
    @GetMapping("/result/{id}")
    public Result invokeResult(@PathVariable String id) throws Exception {
        return commonService.queryResult(id);
    }

}
