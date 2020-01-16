package com.ontology.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.core.transaction.Transaction;
import com.ontology.controller.vo.*;
import com.ontology.entity.Product;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.*;
import com.ontology.service.DataService;
import com.ontology.utils.*;
import io.ont.addon.sdk.DataStorageSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private ProductMapper productMapper;

    private DataStorageSdk dataStorageSdk;

    @PostConstruct
    public void init() throws Exception {
        dataStorageSdk = DataStorageSdk.getInstance(configParam.STORAGE_SERVER_URL);
        dataStorageSdk.init(configParam.APP_DOMAIN, configParam.APP_WIF);
    }

    @Override
    public Map<String, Object> queryData(String action, QueryDataDto req) {
        Integer pageNumber = req.getPageNumber();
        Integer pageSize = req.getPageSize();
        String tag = req.getTag();
        Integer startPage = (pageNumber - 1) * pageSize;
        if (startPage < 0) {
            startPage = 0;
        }
        List<Product> list = productMapper.queryProduct(startPage, pageSize, tag);
        int count = productMapper.queryProductCount(tag);
        List<ProductListResp> resps = new ArrayList<>();
        for (Product one : list) {
            ProductListResp resp = new ProductListResp();
            resp.setDate(one.getCreateTime());
            resp.setId(one.getId());
            resp.setName(one.getName());
            resp.setPrice(one.getPrice());
            resp.setTags(JSONArray.parseArray(one.getTag(), String.class));
            resps.add(resp);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("list", resps);
        return result;
    }

    @Override
    public ProductResp queryDataDetail(String action, Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        ProductResp resp = new ProductResp();
        BeanUtils.copyProperties(product, resp, "tag");
        resp.setTags(JSONArray.parseArray(product.getTag(), String.class));
        return resp;
    }

    @Override
    public List<ProductResp> querySelfProduct(String action, String ontid) {
        List<Product> list = productMapper.selectSelfProduct(ontid);
        List<ProductResp> resps = new ArrayList<>();
        for (Product one : list) {
            ProductResp resp = new ProductResp();
            BeanUtils.copyProperties(one, resp, "tag");
            resp.setTags(JSONArray.parseArray(one.getTag(), String.class));
            resps.add(resp);
        }
        return resps;
    }

    @Override
    public Map<String, Object> registerDataId(String action, DataIdDto req) throws Exception {
        String ontid = req.getOntid();
        MultipartFile file = req.getFile();
        Map<String, Object> map = dataStorageSdk.uploadAndRegisterDataId(ontid, file);

        String invokeId = (String) map.get("id");

        Product product = new Product();
        product.setInvokeId(invokeId);
        product.setOntid(ontid);
        product.setState(0);
        product.setCreateTime(new Date());
        productMapper.insertSelective(product);

        return map;
    }

    @Override
    public Map<String, Object> consumeToken(String action, Integer tokenId) throws Exception {
        return dataStorageSdk.getToken(tokenId);
    }

    @Override
    public Map<String, Object> accessData(String action, String accessToken) {
        return null;
    }


    @Override
    public Map<String, Object> renewJwt(String action, String accessToken) throws Exception {
        return dataStorageSdk.renewToken(accessToken);
    }
}