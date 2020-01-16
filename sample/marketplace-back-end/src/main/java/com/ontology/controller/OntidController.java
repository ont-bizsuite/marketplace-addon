package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageCallbackDto;
import com.ontology.controller.vo.RegisterDto;
import com.ontology.service.OntidService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Api(tags = "Account")
@RestController
@RequestMapping("/api/v1/ontid")
@CrossOrigin
public class OntidController {
    @Autowired
    private OntidService ontidService;

    @ApiOperation(value = "register", notes = "register", httpMethod = "POST")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto req) throws Exception {
        String action = "register";
        Map<String, Object> result = ontidService.register(action,req);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "register callback", notes = "register callback", httpMethod = "POST")
    @PostMapping("/register/callback")
    public Result registerCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "registerCallback";
        String txHash = ontidService.registerCallback(action,req);
        return new Result(action,0, "SUCCESS", txHash);
    }

    @ApiOperation(value = "register result", notes = "register result", httpMethod = "GET")
    @GetMapping("/register/result/{id}")
    public Result registerResult(@PathVariable String id) throws Exception {
        String action = "registerResult";
        return ontidService.registerResult(action,id);
    }

    @ApiOperation(value = "login", notes = "login", httpMethod = "POST")
    @PostMapping("/login")
    public Result login() throws Exception {
        String action = "login";
        Map<String, Object> result = ontidService.login(action);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "login callback", notes = "login callback", httpMethod = "POST")
    @PostMapping("/login/callback")
    public Result loginCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "loginCallback";
        String txHash = ontidService.loginCallback(action,req);
        return new Result(action,0, "SUCCESS", txHash);
    }

    @ApiOperation(value = "login result", notes = "login result", httpMethod = "GET")
    @GetMapping("/login/result/{id}")
    public Result loginResult(@PathVariable String id) {
        String action = "loginResult";
        return ontidService.loginResult(action,id);
    }

}
