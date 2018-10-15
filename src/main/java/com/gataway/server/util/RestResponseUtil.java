package com.gataway.server.util;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李雷 on 2018/7/13.
 */
public class RestResponseUtil {

    private static String NO_TOKEN_RESULT_CODE="1111";
    private static String NO_TOKEN_RESULT_MSG="未登录";

    private static String TOKEN_TIMEOUT_RESULT_CODE="2222";
    private static String TOKEN_TIMEOUT_RESULT_MSG="登陆超时";


    private static String TOKEN_INVALID_RESULT_CODE="3333";
    private static String TOKEN_INVALID_RESULT_MSG="token已失效";

    private static String ILLEGAL_REQUEST_PATH_CODE="4000";
    private static String ILLEGAL_REQUEST_PATH_MSG="非法请求路径";

    private static String CONTENT_TYPE="application/json;charset=UTF-8";

    public static void authErrorResponse(RequestContext requestContext,String resultCode,String resultMsg){
        requestContext.setResponseStatusCode(200);
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().setContentType(CONTENT_TYPE);
        Map<String,String> result = new HashMap<>();
        result.put("resultCode",resultCode);
        result.put("resultMsg",resultMsg);
        requestContext.setResponseBody(JSONObject.toJSONString(ResponseEntity.ok(result)));
    }

    public static void noTokenResponse(RequestContext requestContext){
        authErrorResponse(requestContext,NO_TOKEN_RESULT_CODE,NO_TOKEN_RESULT_MSG);
    }


    public static void tokenTimeOutResponse(RequestContext requestContext){
        authErrorResponse(requestContext,TOKEN_TIMEOUT_RESULT_CODE,TOKEN_TIMEOUT_RESULT_MSG);
    }


    public static void tokenInvalidResponse(RequestContext requestContext){
        authErrorResponse(requestContext,TOKEN_INVALID_RESULT_CODE,TOKEN_INVALID_RESULT_MSG);
    }

    public static void illegalRequestPath(RequestContext requestContext){
        authErrorResponse(requestContext,ILLEGAL_REQUEST_PATH_CODE,ILLEGAL_REQUEST_PATH_MSG);
    }
}
