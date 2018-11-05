package com.gataway.server.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gataway.server.config.DynamicRouteLocator;
import com.gataway.server.fegin.AuthFegin;
import com.gataway.server.util.RestResponseUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by Administrator on 2018/4/14 0014.
 */
@Component
public class SessionHandlerFilter extends ZuulFilter{

    private static final String LOGIN_URI = "/authentication/form";

    private static final String WECHAT_CODE_URL = "/authentication/getWeiXinCodeUrl";

    private static final String DING_CODE_URL = "/authentication/dingcalBack";

    private static String TOKEN_VALUE="tokenValue";

    private static String TOKEN_JTI="jti";

    private static String USER_SESSION="userSession";

    private static final String AUTH_SESSION_KEY="sessionKey";

    private static final String CLIENT_ID_PARAM_NAME = "clientId";

    private static final String OPEN_ID_PARAM_NAME="openId";

    private static final String ID_PARAM_NAME="id";

    private static final String RESULT_CODE="resultCode";

    private static final String TIME_OUT_CODE="2222";

    private static final String NO_SESSION_SERVER_LIST="gateway-no-session-server-list";

    private static final String DIRECT_TRANSMISSION_URL_LIST="gateway-direct-transmission-url-list";


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DynamicRouteLocator dynamicRouteLocator;

    @Autowired
    private AuthFegin authFegin;

    @Override
    public String filterType() {
        return  PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String requestURI = request.getRequestURI();

        Route route = dynamicRouteLocator.getMatchingRoute(requestURI);

        String serverId = route.getId();

        String currentPath = route.getPath();



        BoundHashOperations<String, String, String> hashOperations =  stringRedisTemplate.boundHashOps(DIRECT_TRANSMISSION_URL_LIST);
        String directTransmissionUrls =  hashOperations.get(serverId);
        if(StringUtils.isNotBlank(directTransmissionUrls)){
            List<String> urls = JSONObject.parseObject(directTransmissionUrls,List.class);
            if(urls.contains(currentPath)) return null;
        }

        if (requestURI.contains(LOGIN_URI) || requestURI.contains(WECHAT_CODE_URL) || requestURI.contains(DING_CODE_URL)) return null;


        String token = StringUtils.substringAfter(request.getHeader(HttpHeaders.AUTHORIZATION), "Bearer ");

        if (StringUtils.isEmpty(token)) {
            RestResponseUtil.noTokenResponse(requestContext);
            return null;
        }

        Map<String, Object> authMap =  authFegin.checkToken(token);

        if(TIME_OUT_CODE.equals(authMap.get(RESULT_CODE))){
            RestResponseUtil.tokenTimeOutResponse(requestContext);
            return null;
        }


        Set<String> gatawayClientProperties =  stringRedisTemplate.boundSetOps(NO_SESSION_SERVER_LIST).members();

        if(gatawayClientProperties.contains(String.valueOf(authMap.get(CLIENT_ID_PARAM_NAME)))){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ID_PARAM_NAME,authMap.get(OPEN_ID_PARAM_NAME));

            requestContext.addZuulRequestHeader(USER_SESSION, jsonObject.toJSONString());
            return null;
        }



        String key = String.valueOf(authMap.get(AUTH_SESSION_KEY));
        String tokenValue = (String) authMap.get(TOKEN_JTI);

        String userInfoString = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isBlank(userInfoString)) {
            RestResponseUtil.tokenTimeOutResponse(requestContext);
            return null;
        }


        JSONObject userInfo = JSONObject.parseObject(userInfoString);
        if(!tokenValue.equals(userInfo.getString(TOKEN_VALUE))){
            RestResponseUtil.tokenInvalidResponse(requestContext);
            return null;
        }
        try {
            userInfoString =  URLEncoder.encode(userInfoString, "UTF-8");
            requestContext.addZuulRequestHeader(USER_SESSION, userInfoString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return null;
    }
}
