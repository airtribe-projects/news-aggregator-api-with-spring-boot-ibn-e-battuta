package io.shinmen.airnewsaggregator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Value("${server.port}")
    private String port;

    public void sendVerificationEmail(String to, String token) {
        String url = "http://localhost:" + port + "/api/auth/verify?token=" + token;
        log.info("Hi {}, to confirm your account, please click here : {}", to, url);
    }
}
