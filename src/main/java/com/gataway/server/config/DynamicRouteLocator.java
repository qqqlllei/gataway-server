package com.gataway.server.config;


import com.gataway.server.entity.GataWayRoute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;

import java.util.*;

public class DynamicRouteLocator extends DiscoveryClientRouteLocator {

    private Logger logger = LoggerFactory.getLogger(DynamicRouteLocator.class);

    private ZuulProperties properties;

    private GataWayClientProperties gataWayClientProperties;

    public DynamicRouteLocator(String servletPath, DiscoveryClient discovery, ZuulProperties properties,
                               ServiceInstance localServiceInstance,GataWayClientProperties gataWayClientProperties) {
        super(servletPath, discovery, properties, localServiceInstance);
        this.properties = properties;
        this.gataWayClientProperties= gataWayClientProperties;
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

        List<GataWayRoute> results = gataWayClientProperties.getClients();
        for (GataWayRoute result : results) {
            if (StringUtils.isBlank(result.getPath()) && StringUtils.isBlank(result.getUrl())) {
                continue;
            }

            ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
            zuulRoute.setId(result.getServiceId());
            zuulRoute.setPath(result.getPath());
            zuulRoute.setServiceId(result.getServiceId());
            zuulRoute.setRetryable(("0".equals(result.getRetryable()) ? Boolean.FALSE : Boolean.TRUE));
            zuulRoute.setStripPrefix(("0".equals(result.getStripPrefix()) ? Boolean.FALSE : Boolean.TRUE));
            zuulRoute.setUrl(result.getUrl());
            List<String> sensitiveHeadersList=null;
            if(StringUtils.isNotBlank(result.getSensitiveHeadersList())){
                sensitiveHeadersList = Arrays.asList(result.getSensitiveHeadersList().split(","));
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
