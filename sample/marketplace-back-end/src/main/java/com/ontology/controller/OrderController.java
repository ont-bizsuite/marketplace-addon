package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.entity.Order;
import com.ontology.service.OrderService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "order")
@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "query self order", notes = "query self order", httpMethod = "GET")
    @GetMapping("/query/self/{type}/{ontid}")
    public Result querySelfOrder(@PathVariable Integer type, @PathVariable String ontid) throws Exception {
        String action = "querySelfOrder";
        List<OrderResp> result = orderService.querySelfOrder(action,type,ontid);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "auth product", notes = "auth product", httpMethod = "POST")
    @PostMapping("/auth")
    public Result authOrder(@RequestBody @Valid AuthDto req) throws Exception {
        String action = "authOrder";
        Map<String,Object> result = orderService.authOrder(action,req);
        return new Result(action,0, "SUCCESS", result);
    }


    @ApiOperation(value = "purchase", notes = "purchase", httpMethod = "POST")
    @PostMapping("/take")
    public Result takeOrder(@RequestBody @Valid PurchaseDto req) throws Exception {
        String action = "purchaseOrder";
        Map<String,Object> result = orderService.takeOrder(action,req);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value="confirm", notes="confirm" ,httpMethod="POST")
    @PostMapping("/confirm")
    public Result confirm(@RequestBody @Valid OrderDto req) throws Exception {
        String action = "confirm";
        Map<String,Object> result = orderService.confirm(action,req);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }


    @ApiOperation(value="check token info", notes="check token info" ,httpMethod="GET")
    @GetMapping("/token/balance/{tokenId}")
    @ApiImplicitParam(name = "tokenId",value = "token号", required = true, dataType = "string",paramType = "query")
    public Result getTokenBalance(@PathVariable int tokenId) throws Exception {
        String action = "getTokenBalance";
        Map<String,Object> result = orderService.getTokenBalance(action,tokenId);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }
}
