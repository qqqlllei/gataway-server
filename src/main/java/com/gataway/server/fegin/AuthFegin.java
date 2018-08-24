package com.gataway.server.fegin;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by 李雷 on 2018/7/12.
 */
@FeignClient("auth-server")
public interface AuthFegin {

    @RequestMapping("/authentication/checkToken" )
    Map<String, Object> checkToken(@RequestParam("token") String token);
}
