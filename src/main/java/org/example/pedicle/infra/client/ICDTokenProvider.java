package org.example.pedicle.infra.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ICDTokenProvider {

    private final RestTemplate restTemplate;

    @Value("${icdApi.client-id}")
    private String clientId;
    @Value("${icdApi.client-secret}")
    private String clientSecret;
    private static String ACCESS_TOKEN = "";


    @PostConstruct
    public void initAccessToken() {
        try {
            ACCESS_TOKEN = requestNewAccessToken();
            log.info("[ICDApiClient][initAccessToken()] ICD API Access Token 초기화 완료.");
        } catch (Exception e) {
            log.error("[ICDApiClient][initAccessToken()] ICD API 토큰 초기화 실패", e);
            throw new IllegalStateException("ICD API 토큰 초기화 중 오류 발생", e);
        }
    }

    private String requestNewAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "client_credentials");
        params.add("scope", "icdapi_access");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(ICDUri.TOKEN_URL, request, Map.class);
        Map<String, Object> body = response.getBody();

        if (body == null || body.get("access_token") == null) {
            throw new IllegalStateException("ICD 토큰 응답에 access_token이 없습니다.");
        }

        return (String) body.get("access_token");
    }

    public String getAccessToken() {
        if (!ACCESS_TOKEN.isEmpty()) {
            return ACCESS_TOKEN;
        }
        return requestNewAccessToken();
    }

}
