package com.example.Backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@Data
public class ECPayConfig {

    @Value("${ecpay.merchant.id}")
    private String merchantId;

    @Value("${ecpay.hash.key}")
    private String hashKey;

    @Value("${ecpay.hash.iv}")
    private String hashIv;

    @Value("${ecpay.api.url}")
    private String apiUrl;

    @Value("${ecpay.return.url}")
    private String returnUrl;

    @Value("${ecpay.order.result.url}")
    private String orderResultUrl;

    @Value("${ecpay.client.back.url}")
    private String clientBackUrl;
}