package com.gataway.server.filter;

import com.gataway.server.util.RestResponseUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by 李雷 on 2018/9/19.
 */
@Component
public class InternalRequestFilter extends ZuulFilter{

    private static final String INTERNAL_REQUEST_URI = "/openApi";

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 2;
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
        if (requestURI.contains(INTERNAL_REQUEST_URI)){
            RestResponseUtil.illegalRequestPath(requestContext);
        }

        return null;
    }
}
