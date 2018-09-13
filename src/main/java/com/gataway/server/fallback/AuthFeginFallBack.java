package com.gataway.server.fallback;

import com.alibaba.fastjson.JSONObject;
import com.netflix.client.ClientException;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by 李雷 on 2018/8/8.
 */
@Component
public class AuthFeginFallBack implements FallbackProvider {

    private Logger logger = LoggerFactory.getLogger(AuthFeginFallBack.class);

    @Override
    public ClientHttpResponse fallbackResponse(Throwable cause) {
        if (cause instanceof HystrixTimeoutException) {
            return response(HttpStatus.GATEWAY_TIMEOUT);
        }else if(cause instanceof ClientException){
            return response(HttpStatus.REQUEST_TIMEOUT);
        } else {
            return fallbackResponse();
        }
    }

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return response(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ClientHttpResponse response(final HttpStatus status) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() {
                return status;
            }

            @Override
            public int getRawStatusCode() {
                return status.value();
            }

            @Override
            public String getStatusText() {
                return status.getReasonPhrase();
            }

            @Override
            public void close() {
                logger.info(getStatusText());
            }

            @Override
            public InputStream getBody() {
                JSONObject body = new JSONObject();
                body.put("code","200");
                body.put("message","微服务故障, 请稍后再试");
                body.put("error",getRawStatusCode());
                return new ByteArrayInputStream(body.toJSONString().getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
