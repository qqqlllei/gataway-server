package com.gataway.server.config;


import com.gataway.server.entity.GataWayRoute;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/11 0011.
 */
@ConfigurationProperties(prefix = "zuul")
public class GataWayClientProperties {

    private List<GataWayRoute> clients = new ArrayList<>();

    public List<GataWayRoute> getClients() {
        return clients;
    }

    public void setClients(List<GataWayRoute> clients) {
        this.clients = clients;
    }
}
