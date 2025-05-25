package org.example.pedicle.interfaces.controller;

import lombok.RequiredArgsConstructor;
import org.example.pedicle.domain.DiagnosisService;
import org.example.pedicle.infra.client.dto.ICD10ApiRes;
import org.example.pedicle.infra.client.dto.ICD11ApiRes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    @GetMapping("/hello")
    public String hello() {
        return "hello~~";
    }


    /**
     * Use CASE
     * 1. 질병명
     * 2. 증상
     * 3. ICD 10 코드
     *       제공할 수 있는 것들 - 유사 코드(ICD10), 상위 병명, 하위 병명, (해부학적 위치, 증상
     * 4.
     */
    @GetMapping("/diagnosis")
    public ICD10ApiRes.ReleaseIdApiRes search() {
        //TODO 1. 질병명으로 거
        return diagnosisService.getDeceasesOfICD11FromIcd10("dsf");
//        return "hello~~";
    }

//    @GetMapping("/auth")
//    public String fetch() {
//        //TODO 1. 질병명으로 거
//        return diagnosisService.fetchAccessToken();
////        return "hello~~";
//    }

    @GetMapping("/code")
    public ICD10ApiRes.CodeForReleaseIdApiRes getCodeInfoForVersion(@RequestParam("icd10Code") String icd10Code) {
        return diagnosisService.getCodeInfoForVersion(icd10Code);
//        return "hello~~";
    }

    @GetMapping("/code/version")
    public ICD10ApiRes.CodeApiRes getCodeInfo(@RequestParam("icd10Code") String icd10Code) {
        return diagnosisService.getCodeInfo(icd10Code);
//        return "hello~~";
    }

    @GetMapping("/mms/entity")
    public ICD11ApiRes.EntityDetailRes getLinearizedEntityInfo(@RequestParam("entityId") String entityId) {
        return diagnosisService.getLinearizedEntityInfo(entityId);
    }

    @GetMapping("/entity")
    public ICD11ApiRes.EntityRes getEntityInfo(@RequestParam("entityId") String entityId) {
        return diagnosisService.getEntityInfo(entityId);
    }
}
