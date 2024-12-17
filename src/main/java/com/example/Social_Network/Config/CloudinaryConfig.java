package com.example.Social_Network.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Value("${cloud.name}")
    private String name;
    @Value("${cloud.api-key}")
    private String apiKey;
    @Value("${cloud.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", name,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}
