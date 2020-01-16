package com.ontology.service;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageCallbackDto;
import com.ontology.controller.vo.RegisterDto;

import java.util.Map;


public interface OntidService {

    Map<String, Object> register(String action, RegisterDto req) throws Exception;

    String registerCallback(String action, MessageCallbackDto req) throws Exception;

    Result registerResult(String action, String id);

    Map<String, Object> login(String action) throws Exception;

    String loginCallback(String action, MessageCallbackDto req);

    Result loginResult(String action, String id);

}
