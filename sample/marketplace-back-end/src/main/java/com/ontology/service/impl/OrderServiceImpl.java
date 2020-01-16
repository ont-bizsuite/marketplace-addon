package com.ontology.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ontology.controller.vo.*;
import com.ontology.entity.Order;
import com.ontology.entity.Product;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.OrderMapper;
import com.ontology.mapper.ProductMapper;
import com.ontology.service.OrderService;
import com.ontology.utils.*;
import io.ont.addon.sdk.MarketplaceSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;

    private MarketplaceSdk marketplaceSdk;

    @PostConstruct
    public void init() throws Exception {
        marketplaceSdk = MarketplaceSdk.getInstance(configParam.MARKETPLACE_SERVER_URL);
        marketplaceSdk.init(configParam.APP_DOMAIN, configParam.APP_WIF);
    }

    @Override
    public List<OrderResp> querySelfOrder(String action, Integer type, String ontid) {
        String ownerType = "";
        if (type == 1) {
            ownerType = "provider";
        } else if (type == 2) {
            ownerType = "demander";
        } else {
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }

        List<Order> list = orderMapper.querySelfOrder(ownerType,ontid);
        List<OrderResp> result = new ArrayList<>();
        for (Order order : list) {
            OrderResp resp = new OrderResp();
            BeanUtils.copyProperties(order,resp);
            List<String> tokenIds = JSONObject.parseArray(order.getTokenId(), String.class);
            resp.setTokenId(tokenIds.get(0));
            if (new Date().after(new Date(order.getCreateTime().getTime() + Constant.ORDER_EXPIRE))) {
                resp.setIsExpire(true);
            }
            result.add(resp);
        }
        return result;
    }

    @Override
    public Map<String, Object> authOrder(String action, AuthDto req) throws Exception {
        // save to DB
        Integer id = req.getId();
        String priceStr = req.getPrice().toString();
        Integer amount = req.getAmount();
        String name = req.getName();
        String description = req.getDescription();
        List<String> tags = req.getTags();
        String dataId = req.getDataId();
        String symbol = req.getSymbol();
        Integer accessCount = req.getAccessCount();
        Integer transferCount = req.getTransferCount();
        Long expireTime = req.getExpireTime();
        String providerAddr = req.getProvider().substring(8);
        String mpAddr = Constant.JUDGER_ADDRESS;
        List<String> list = new ArrayList<>();
        list.add(Constant.JUDGER_ADDRESS);

        Product product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Map<String, Object> map = marketplaceSdk.authOrder(dataId, symbol, name, amount, req.getPrice(), transferCount, accessCount, expireTime, providerAddr, mpAddr, list);
        String invokeId = (String) map.get("id");

        product.setCreateTime(new Date());
        product.setPrice(priceStr);
        product.setAmount(amount);
        product.setDescription(description);
        product.setName(name);
        product.setTag(JSON.toJSONString(tags));
        product.setJudger(Constant.JUDGER_ADDRESS);
        product.setState(2);
        product.setInvokeId(invokeId);
        productMapper.updateByPrimaryKeySelective(product);

        return map;
    }

    @Override
    public Map<String, Object> takeOrder(String action, PurchaseDto req) throws Exception {

        String authId = req.getAuthId();
        Integer tokenAmount = req.getTokenAmount();
        String priceStr = req.getPrice().toString();
        String provider = req.getProvider();
        String demander = req.getDemander();

        Map<String, Object> map = marketplaceSdk.takeOrder(authId, demander.substring(8), tokenAmount, Constant.JUDGER_ADDRESS);
        String invokeId = (String) map.get("id");
        Order order = new Order();
        order.setInvokeId(invokeId);
        order.setPrice(priceStr);
        order.setProvider(provider);
        order.setDemander(demander);
        order.setState(1);
        order.setCreateTime(new Date());
        orderMapper.insertSelective(order);

        return map;
    }

    @Override
    public Map<String, Object> confirm(String action, OrderDto req) throws Exception {
        String orderId = req.getOrderId();
        Order order = new Order();
        order.setOrderId(orderId);
        order = orderMapper.selectOne(order);
        if (order == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Map<String, Object> map = marketplaceSdk.confirmOrder(orderId);
        String invokeId = (String) map.get("id");

        order.setInvokeId(invokeId);
        order.setState(3);
        orderMapper.updateByPrimaryKeySelective(order);

        return map;
    }


    @Override
    public Map<String, Object> getTokenBalance(String action, int tokenId) throws Exception {
        return marketplaceSdk.getTokenBalance(tokenId);
    }

}
