package com.gataway.server.entity;

public class GataWayRoute {
    private String path;
    private String serviceId;
    private String url;
    private String stripPrefix;
    private String retryable;
    private String sensitiveHeadersList;
    private String status;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(String stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public String getRetryable() {
        return retryable;
    }

    public void setRetryable(String retryable) {
        this.retryable = retryable;
    }

    public String getSensitiveHeadersList() {
        return sensitiveHeadersList;
    }

    public void setSensitiveHeadersList(String sensitiveHeadersList) {
        this.sensitiveHeadersList = sensitiveHeadersList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
