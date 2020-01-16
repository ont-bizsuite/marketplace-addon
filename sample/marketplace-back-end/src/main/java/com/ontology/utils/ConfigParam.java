package com.ontology.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {


	@Value("${service.restfulUrl}")
	public String RESTFUL_URL;

	@Value("${payer.addr}")
	public String PAYER_ADDRESS;

	@Value("${payer.wif}")
	public String PAYER_WIF;

	@Value("${signing.server.url}")
	public String SIGNING_SERVER_URL;

	@Value("${storage.server.url}")
	public String STORAGE_SERVER_URL;

	@Value("${marketplace.server.url}")
	public String MARKETPLACE_SERVER_URL;
	/**
	 *  APP info
	 */
	@Value("${app.wif}")
	public String APP_WIF;
	@Value("${app.domain}")
	public String APP_DOMAIN;
}