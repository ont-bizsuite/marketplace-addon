package com.ontology.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.entity.Invoke;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.InvokeMapper;
import com.ontology.service.CommonService;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.ErrorInfo;
import io.ont.addon.sdk.MarketplaceSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {
    @Autowired
    private ConfigParam configParam;

    @Autowired
    private InvokeMapper invokeMapper;

    private MarketplaceSdk marketplaceSdk;

    @PostConstruct
    public void init() throws Exception {
        marketplaceSdk = MarketplaceSdk.getInstance(configParam.MARKETPLACE_SERVER_URL);
    }

    @Override
    public String callbackAndGetParams(String action, String id) {
        Invoke invoke = invokeMapper.selectByPrimaryKey(id);
        if (invoke == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        return invoke.getParams();
    }

    @Override
    public Result queryResult(String id) throws Exception {
        Map<String,Object> map = marketplaceSdk.orderResult(id);
        String invokeResult = (String) map.get("result");

        Map<String, Object> result = new HashMap<>();
        result.put("result", invokeResult);
        if (map.containsKey("consumeToken")) {
            result.put("jwt", (String) map.get("consumeToken"));
        }

        return new Result("queryResult", ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }
}
