package com.gataway.server.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 李雷 on 2018/7/12.
 */
@Component
public class AuthTokenFilter extends ZuulFilter {

    private static final String LOGOUT_URI = "/oauth/token";
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String requestURI = request.getRequestURI();

        if (requestURI.contains(LOGOUT_URI)) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            requestContext.addZuulRequestHeader(HttpHeaders.AUTHORIZATION, authHeader);
        }


        return null;
    }
}
