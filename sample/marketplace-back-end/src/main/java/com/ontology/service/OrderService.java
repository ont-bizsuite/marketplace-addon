package com.ontology.service;


import com.ontology.controller.vo.*;

import java.util.List;
import java.util.Map;

public interface OrderService {

    List<OrderResp> querySelfOrder(String action, Integer type, String ontid);

    Map<String, Object> authOrder(String action, AuthDto req) throws Exception;

    Map<String, Object> takeOrder(String action, PurchaseDto req) throws Exception;

    Map<String, Object> confirm(String action, OrderDto req) throws Exception;


    Map<String, Object> getTokenBalance(String action, int tokenId) throws Exception;

}
