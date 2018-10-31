package com.gataway.server.config;


import com.alibaba.fastjson.JSONObject;
import com.gataway.server.entity.GataWayRoute;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

public class DynamicRouteLocator extends DiscoveryClientRouteLocator {

    private static final String GATEWAY_CLIENT_PROPERTIES ="gateway-client-properties";

    private ZuulProperties properties;

    private StringRedisTemplate stringRedisTemplate;

    public DynamicRouteLocator(String servletPath, DiscoveryClient discovery, ZuulProperties properties,
                               ServiceInstance localServiceInstance,StringRedisTemplate stringRedisTemplate) {
        super(servletPath, discovery, properties, localServiceInstance);
        this.properties = properties;
        this.stringRedisTemplate= stringRedisTemplate;
    }


    @Override
    protected LinkedHashMap<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();
        routesMap.putAll(super.locateRoutes());
        routesMap.putAll(locateRoutesFromProperties());
        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (StringUtils.isNotBlank(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, entry.getValue());
        }
        return values;
    }

    private Map<String, ZuulProperties.ZuulRoute> locateRoutesFromProperties() {
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();


        Set<String> gatawayClientProperties =  stringRedisTemplate.boundSetOps(GATEWAY_CLIENT_PROPERTIES).members();

        for (String client:gatawayClientProperties) {
            GataWayRoute gataWayRoute = JSONObject.parseObject(client,GataWayRoute.class);
            if (StringUtils.isBlank(gataWayRoute.getPath()) && StringUtils.isBlank(gataWayRoute.getUrl())) {
                continue;
            }

            ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
            zuulRoute.setId(gataWayRoute.getServiceId());
            zuulRoute.setPath(gataWayRoute.getPath());
            zuulRoute.setServiceId(gataWayRoute.getServiceId());
            zuulRoute.setRetryable(("0".equals(gataWayRoute.getRetryable()) ? Boolean.FALSE : Boolean.TRUE));
            zuulRoute.setStripPrefix(("0".equals(gataWayRoute.getStripPrefix()) ? Boolean.FALSE : Boolean.TRUE));
            zuulRoute.setUrl(gataWayRoute.getUrl());
            List<String> sensitiveHeadersList=null;
            if(StringUtils.isNotBlank(gataWayRoute.getSensitiveHeadersList())){
                sensitiveHeadersList = Arrays.asList(gataWayRoute.getSensitiveHeadersList().split(","));
            }

            if (sensitiveHeadersList != null) {
                Set<String> sensitiveHeaderSet = new HashSet<>();
                sensitiveHeadersList.forEach(sensitiveHeader -> sensitiveHeaderSet.add(sensitiveHeader));
                zuulRoute.setSensitiveHeaders(sensitiveHeaderSet);
                zuulRoute.setCustomSensitiveHeaders(true);
            }

            routes.put(zuulRoute.getPath(), zuulRoute);
        }
        return routes;
    }
}
