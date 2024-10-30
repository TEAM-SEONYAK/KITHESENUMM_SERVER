package org.sopt.seonyakServer.global.config;

import org.sopt.seonyakServer.SeonyakServerApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = SeonyakServerApplication.class)
public class FeignConfig {

}
