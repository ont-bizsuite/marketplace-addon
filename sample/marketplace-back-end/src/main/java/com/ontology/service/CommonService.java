package com.ontology.service;

import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageCallbackDto;

import java.util.Map;

public interface CommonService {

    String callbackAndGetParams(String action, String id);

    Result queryResult(String id) throws Exception;
}
