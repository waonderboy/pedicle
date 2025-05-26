package org.example.pedicle.infra.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedicle.infra.client.dto.ICD10ApiRes;
import org.example.pedicle.infra.client.dto.ICD11ApiRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ICDApiClient {
    /**
     * WHO ICD(The International Statistical Classification of Diseases and Related Health Problems)  API Client
     * 세계 보건기구에서 발표한 국제 질병 분류(ICD) 관련 문서를 조회할 수 있는 API
     */
    private final RestTemplate restTemplate;
    private final ICDTokenProvider icdTokenProvider;
    private final ObjectMapper objectMapper;

    //TODO - 버젼 체크

    // ICD 11 Linearized Entity `Info`
    public ICD11ApiRes.EntityDetailRes getLinearizedEntityInfo(String entityId) {
        String url = UriComponentsBuilder.fromUriString(ICDUri.ICD11_LINEARIZATION_INFO)
                .buildAndExpand("2024-01", "mms", entityId)
                .toUriString();
        log.info("request uri = {}", url);
        return requestToICDApi(HttpMethod.GET, url, ICD11ApiRes.EntityDetailRes.class);
    }

    public ICD11ApiRes.EntityDetailRes getLinearizedEntityInfoByUri(String uri) {
        String url = UriComponentsBuilder.fromUriString(uri)
                .toUriString();
        log.info("request uri = {}", url);
        return requestToICDApi(HttpMethod.GET, url, ICD11ApiRes.EntityDetailRes.class);
    }

    // ICD 11 Entity `Info`
    public ICD11ApiRes.EntityRes getFoundationInfo(String entityId) {
        String url = UriComponentsBuilder.fromUriString(ICDUri.ICD11_FOUNDATION_INFO)
                .buildAndExpand(entityId)
                .toUriString();
        log.info("request uri = {}", url);
        return requestToICDApi(HttpMethod.GET, url, ICD11ApiRes.EntityRes.class);
    }


    // ICD 10 Version `Info`
    public ICD10ApiRes.ReleaseIdApiRes getVersionInfo(String version) {
        String url = UriComponentsBuilder.fromUriString(ICDUri.ICD10_VERSION)
                .buildAndExpand(version)
                .toUriString();
        log.info("request uri = {}", url);
        return requestToICDApi(HttpMethod.GET, url, ICD10ApiRes.ReleaseIdApiRes.class);
    }


     // ICD 10 Code `Info`
    public ICD10ApiRes.CodeApiRes getCodeInfo(String icd10Code) {
        String url = UriComponentsBuilder.fromUriString(ICDUri.ICD10_CODE_INFO)
                .buildAndExpand(icd10Code)
                .toUriString();
        log.info("request uri = {}", url);
        return requestToICDApi(HttpMethod.GET, url, ICD10ApiRes.CodeApiRes.class);
    }

    // ICD 10 Code `Info` For Version
    public ICD10ApiRes.CodeForReleaseIdApiRes getCodeInfoForVersion(String icd10Code) {
        String url = UriComponentsBuilder.fromUriString(ICDUri.ICD10_CODE_INFO_FOR_VERSION)
                .buildAndExpand("2019", icd10Code)
                .toUriString();

        log.info("request uri = {}", url);
        return requestToICDApi(HttpMethod.GET, url, ICD10ApiRes.CodeForReleaseIdApiRes.class);
    }

    private <T> T requestToICDApi(HttpMethod httpMethod, String url, Class<T> responseEntity) {
        HttpEntity<Void> headers = new HttpEntity<>(buildHeaders());
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    httpMethod,
                    headers,
                    responseEntity
            );
//            log.info("ICD API Response: {}", response.getBody());
            log.info("ICD API Response:\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getBody()));

            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("ICD API 호출 실패. 상태코드: {}, 응답: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("ICD API 호출 실패", e);
        } catch (JsonProcessingException e) {
            log.error("JSON Parsing 실패");
            throw new RuntimeException("JSON Parsing 실패", e);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("API-Version", "v2");
        headers.set("Accept-Language", "en");
        headers.setBearerAuth(icdTokenProvider.getAccessToken());
        return headers;
    }

}
