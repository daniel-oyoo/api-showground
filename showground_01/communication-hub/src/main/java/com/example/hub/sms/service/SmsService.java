package com.example.hub.sms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.example.hub.sms.dto.SmsRequest;

import org.springframework.http.MediaType;
import java.util.Base64;
import java.util.Map;

@Service
public class SmsService {

    @Value("${app.twilio.account-sid}") private String sid;
    @Value("${app.twilio.auth-token}") private String token;
    @Value("${app.twilio.from-number}") private String from;

    private final RestClient client = RestClient.create();

    public String send(SmsRequest req) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("To", req.getTo());
        form.add("From", from);
        form.add("Body", req.getMessage());

        Map response = client.post()
                .uri("https://api.twilio.com/2010-04-01/Accounts/{sid}/Messages.json", sid)
                .header("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString((sid + ":" + token).getBytes()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        return "SMS sent: " + (response != null ? response.get("sid") : "unknown");
    }
}