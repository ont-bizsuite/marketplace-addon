package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.service.DataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;


@Api(tags = "data")
@Slf4j
@RestController
@RequestMapping("/api/v1/data")
@CrossOrigin
public class DataController {
    @Autowired
    private DataService dataService;

    @ApiOperation(value = "query product", notes = "query product", httpMethod = "POST")
    @PostMapping("/query")
    public Result queryData(@Valid @RequestBody QueryDataDto req) throws Exception {
        String action = "queryData";
        Map<String, Object> result = dataService.queryData(action, req);
        return new Result(action, 0, "SUCCESS", result);
    }

    @ApiOperation(value = "query product detail", notes = "query product detail", httpMethod = "GET")
    @GetMapping("/query/{id}")
    public Result queryDataDetail(@PathVariable Integer id) throws Exception {
        String action = "queryDataDetail";
        ProductResp result = dataService.queryDataDetail(action, id);
        return new Result(action, 0, "SUCCESS", result);
    }

    @ApiOperation(value = "query self product", notes = "query self product", httpMethod = "GET")
    @GetMapping("/query/self/{ontid}")
    public Result querySelfProduct(@PathVariable String ontid) throws Exception {
        String action = "querySelfProduct";
        List<ProductResp> result = dataService.querySelfProduct(action, ontid);
        return new Result(action, 0, "SUCCESS", result);
    }

    @ApiOperation(value = "upload and register data id", notes = "upload and register data id", httpMethod = "POST")
    @PostMapping("/dataId/register")
    public Result registerDataId(DataIdDto req) throws Exception {
        String action = "registerDataId";
        log.info("ontid:{}", req.getOntid());
        Map<String, Object> result = dataService.registerDataId(action, req);
        return new Result(action, 0, "SUCCESS", result);
    }


    @ApiOperation(value = "verify token and generate jwt", notes = "verify token and generate jwt", httpMethod = "POST")
    @PostMapping("/token/{tokenId}")
    public Result consumeToken(@PathVariable Integer tokenId) throws Exception {
        String action = "consumeToken";
        Map<String, Object> result = dataService.consumeToken(action, tokenId);
        return new Result(action, 0, "SUCCESS", result);
    }


    @ApiOperation(value = "renew jwt", notes = "renew jwt", httpMethod = "POST")
    @PostMapping("/renew")
    public Result renewJwt(@RequestBody AccessDto req) throws Exception {
        String action = "renewJwt";
        String accessToken = req.getAccessToken();
        Map<String, Object> result = dataService.renewJwt(action, accessToken);
        return new Result(action, 0, "SUCCESS", result);
    }

}