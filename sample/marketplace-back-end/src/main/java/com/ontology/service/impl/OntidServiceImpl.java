package com.ontology.service.impl;

import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageCallbackDto;
import com.ontology.controller.vo.RegisterDto;
import com.ontology.entity.Invoke;
import com.ontology.entity.Login;
import com.ontology.entity.Register;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.InvokeMapper;
import com.ontology.mapper.LoginMapper;
import com.ontology.mapper.RegisterMapper;
import com.ontology.service.OntidService;
import com.ontology.utils.*;
import io.ont.addon.signing.sdk.SigningSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OntidServiceImpl implements OntidService {
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private InvokeMapper invokeMapper;
    @Autowired
    private ConfigParam configParam;

    private SigningSdk signingSdk;

    @PostConstruct
    public void init() throws Exception {
        signingSdk = SigningSdk.getInstance(configParam.SIGNING_SERVER_URL);
        signingSdk.init(configParam.APP_DOMAIN, configParam.APP_WIF);
        signingSdk.setBlockChainUrl(configParam.RESTFUL_URL);
    }

    @Override
    public Map<String, Object> register(String action, RegisterDto req) throws Exception {
        String userName = req.getUserName();
        String id = UUID.randomUUID().toString();
        // check duplicate
        Register register = new Register();
        register.setUserName(userName);
        register.setState(Constant.STATE_SUCCESS);
        List<Register> list = registerMapper.select(register);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MarketplaceException(action, ErrorInfo.USER_ALREADY_EXIST.descCN(), ErrorInfo.USER_ALREADY_EXIST.descEN(), ErrorInfo.USER_ALREADY_EXIST.code());
        }

        register.setId(id);
        register.setState(null);
        register.setCreateTime(new Date());
        registerMapper.insertSelective(register);

        List<Map<String, Object>> argsList = new ArrayList<>();
        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "register");
        arg0.put("value", "String:ontid");
        argsList.add(arg0);
        String params = signingSdk.constructMessage(argsList);
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(params);
        invokeMapper.insertSelective(invoke);

        String signature = signingSdk.sign(params);
        Map<String, Object> qrCodeParams = signingSdk.invoke(Constant.ACTION_REGISTER, id);
        qrCodeParams.put("signature", signature);

        Map<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("qrCode",qrCodeParams);

        return result;
    }


    @Override
    public String registerCallback(String action, MessageCallbackDto req) {
        String id = req.getId();
        Boolean verified = req.getVerified();
        String ontid = req.getOntid();
        Register register = registerMapper.selectByPrimaryKey(id);
        if (register == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        if (verified) {
            String userName = register.getUserName();
            List<Register> list = registerMapper.selectByOntidAndUserName(ontid, userName);
            if (!CollectionUtils.isEmpty(list)) {
                register.setState(Constant.REGISTER_STATE_ALREADY_EXIST);
            } else {
                register.setOntid(ontid);
                register.setState(Constant.STATE_SUCCESS);
            }
        } else {
            register.setState(Constant.STATE_FAILURE);
        }
        registerMapper.updateByPrimaryKeySelective(register);
        return null;
    }

    @Override
    public Result registerResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Register register = registerMapper.selectByPrimaryKey(id);
        if (register == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = register.getState();
        if (state != null) {
            result.put("result", state.toString());
            result.put("ontid", register.getOntid());
            result.put("userName", register.getUserName());
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @Override
    public Map<String, Object> login(String action) throws Exception {
        String id = UUID.randomUUID().toString();
        Login login = new Login();
        login.setId(id);
        login.setCreateTime(new Date());
        loginMapper.insertSelective(login);

        List<Map<String, Object>> argsList = new ArrayList<>();
        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "login");
        arg0.put("value", "String:ontid");
        argsList.add(arg0);
        String params = signingSdk.constructMessage(argsList);
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(params);
        invokeMapper.insertSelective(invoke);

        String signature = signingSdk.sign(params);
        Map<String, Object> qrCodeParams = signingSdk.invoke(Constant.ACTION_LOGIN, id);
        qrCodeParams.put("signature", signature);

        Map<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("qrCode",qrCodeParams);

        return result;
    }

    @Override
    public String loginCallback(String action, MessageCallbackDto req) {
        String id = req.getId();
        Boolean verified = req.getVerified();
        String ontid = req.getOntid();
        Login login = loginMapper.selectByPrimaryKey(id);
        if (login == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        if (verified) {
            // check register info
            Register register = new Register();
            register.setOntid(ontid);
            register = registerMapper.selectOne(register);
            if (register == null) {
                login.setState(Constant.LOGIN_STATE_NOT_REGISTER);
            } else {
                login.setOntid(ontid);
                login.setState(Constant.STATE_SUCCESS);
                login.setUserName(register.getUserName());
            }
        } else {
            login.setState(Constant.STATE_FAILURE);
        }
        loginMapper.updateByPrimaryKey(login);
        return null;
    }

    @Override
    public Result loginResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Login login = loginMapper.selectByPrimaryKey(id);
        if (login == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = login.getState();
        if (state != null) {
            result.put("result", state.toString());
            result.put("ontid", login.getOntid());
            result.put("userName", login.getUserName());
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

}
