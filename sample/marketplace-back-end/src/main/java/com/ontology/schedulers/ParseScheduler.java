package com.ontology.schedulers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ontology.entity.Order;
import com.ontology.entity.Product;
import com.ontology.mapper.OrderMapper;
import com.ontology.mapper.ProductMapper;
import com.ontology.utils.ConfigParam;
import io.ont.addon.sdk.DataStorageSdk;
import io.ont.addon.sdk.MarketplaceSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
@EnableScheduling
public class ParseScheduler extends BaseScheduler {

    @Autowired
    private ConfigParam configParam;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;

    private DataStorageSdk dataStorageSdk;
    private MarketplaceSdk marketplaceSdk;

    @PostConstruct
    public void init() {
        dataStorageSdk = DataStorageSdk.getInstance(configParam.STORAGE_SERVER_URL);
        marketplaceSdk = MarketplaceSdk.getInstance(configParam.MARKETPLACE_SERVER_URL);
    }

    /**
     * query upload result
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void uploadSchedule() throws Exception {
        log.info("uploadSchedule : {}", Thread.currentThread().getName());
        List<Product> list = productMapper.selectUpload();
        for (Product product : list) {
            String invokeId = product.getInvokeId();
            Map<String, Object> map = dataStorageSdk.invokeResult(invokeId);
            String result = (String) map.get("result");
            if ("2".equals(result)) {
                String dataId = (String) map.get("dataId");
                product.setDataId(dataId);
                product.setState(1);
                productMapper.updateByPrimaryKeySelective(product);
            }
        }
    }

    /**
     * query auth result
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void authSchedule() throws Exception {
        log.info("authSchedule : {}", Thread.currentThread().getName());
        List<Product> list = productMapper.selectAuth();
        for (Product product : list) {
            String invokeId = product.getInvokeId();
            Map<String, Object> map = dataStorageSdk.invokeResult(invokeId);
            String result = (String) map.get("result");
            if ("2".equals(result)) {
                String authId = (String) map.get("authId");
                product.setAuthId(authId);
                product.setState(3);
                productMapper.updateByPrimaryKeySelective(product);
            }
        }
    }

    /**
     * query purchase result
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void purchaseSchedule() throws Exception {
        log.info("purchaseSchedule : {}", Thread.currentThread().getName());
        List<Order> list = orderMapper.selectPurchase();
        for (Order order : list) {
            String invokeId = order.getInvokeId();
            Map<String, Object> map = marketplaceSdk.orderResult(invokeId);
            String result = (String) map.get("result");
            if ("2".equals(result)) {
                String orderId = (String) map.get("orderId");
                Object tokenId = map.get("tokenId");
                order.setOrderId(orderId);
                order.setTokenId(JSON.toJSONString(tokenId));
                order.setState(2);
                orderMapper.updateByPrimaryKeySelective(order);
            }
        }
    }

    /**
     * query confirm result
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void confirmSchedule() throws Exception {
        log.info("confirmSchedule : {}", Thread.currentThread().getName());
        List<Order> list = orderMapper.selectConfirm();
        for (Order order : list) {
            String invokeId = order.getInvokeId();
            Map<String, Object> map = marketplaceSdk.orderResult(invokeId);
            String result = (String) map.get("result");
            if ("2".equals(result)) {
                order.setState(4);
                orderMapper.updateByPrimaryKeySelective(order);
            }
        }
    }
}
