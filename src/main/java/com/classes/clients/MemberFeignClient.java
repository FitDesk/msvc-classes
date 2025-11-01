package com.classes.clients;

import com.classes.config.FeignConfig;
import com.classes.dtos.external.MemberInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;


@FeignClient(
    name = "msvc-members",  
    // url = "http://localhost:9098",  
    configuration = FeignConfig.class
)
public interface MemberFeignClient {


    @GetMapping("/member/{memberId}/info")
    MemberInfoDTO getMemberInfo(@PathVariable("memberId") UUID memberId);
}
