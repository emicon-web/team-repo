package com.bezkoder.springjwt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class RestServiceUtil {
    private static final Logger logger = LoggerFactory.getLogger(RestServiceUtil.class);

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity postResponse(String url, Class<?> responseType, Object objectRequest) {

        ResponseEntity response = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity requestEntity = new HttpEntity(objectRequest, requestHeaders);
        logger.info("Post Response: " + url);
        logger.info("Request entity: " + requestEntity);
        response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
        return response;
    }
}
