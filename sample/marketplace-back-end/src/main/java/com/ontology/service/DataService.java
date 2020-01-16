package com.ontology.service;

import com.ontology.controller.vo.DataIdDto;
import com.ontology.controller.vo.ProductResp;
import com.ontology.controller.vo.QueryDataDto;

import java.util.List;
import java.util.Map;

public interface DataService {

    Map<String, Object> queryData(String action, QueryDataDto req);

    ProductResp queryDataDetail(String action, Integer id);

    List<ProductResp> querySelfProduct(String action, String ontid);

    Map<String, Object> registerDataId(String action, DataIdDto req) throws Exception;

    Map<String, Object> consumeToken(String action, Integer tokenId) throws Exception;

    Map<String, Object> accessData(String action, String accessToken);

    Map<String, Object> renewJwt(String action, String accessToken) throws Exception;


}
