package org.example.pedicle.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedicle.infra.client.ICDApiClient;
import org.example.pedicle.infra.client.dto.ICD10ApiRes;
import org.example.pedicle.infra.client.dto.ICD11ApiRes;
import org.example.pedicle.infra.repository.ICD10InfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisService {
    private final ICDApiClient icdApiClient;
    private final ICD10InfoRepository icd10InfoRepository;

    /**
     * USE CASE
     * 1. 부위 입력 -> ICD 11 추천(boolean)
     * 2. ICD10 질병 코드로 검색 -> ICD 11 추천(boolean) -> 부위 -> 상세 부위
     * 3. 진단명 검색 (키워드)-> ICD 11 추천(boolean) -> 부위 -> 상세 부위
     *
     * 4. 전체 키워드 검색 (ICD 11 API)
     * 5. 증상 검색 (ICD 11 API)
     *
     * 기본 검색 기능에서 ICD 11 추천(boolean) 을 할지 말지 ㄱ
     * ICD10 -> 11
     * ICD11 -> 10
     *
     * ******* 우선 `진단명 검색` , `질병 코드 검색` 으로 추천부터 개발
     */

    private void getDiagnosisInfo() {
        //TODO 1. 특정 키워드로 진단명 및 진단 코드 조회 Diagnosis
        //TODO 2. 진단 코드와 유사 코드 조회 ICD10SimilarCodeInfo
        //TODO 3.
    }

    /**
     * 1. ICD 10으로 검색
     * <p>
     * 목적 : 딱봐도 ICD 10 코드중 M511 인데, 어떤 후보군 들로 고려할 수 있을까?
     * 좀 더 심층적인 탐구를 추천
     *
     * @return
     */
    // 10의 코드에 포함된 11의 상병 URL 가져옴
    public ICD10ApiRes.ReleaseIdApiRes getDeceasesOfICD11FromIcd10(String icd10Code) {
//        ICD10Info icd10Info= icd10InfoRepository.findByIcd10Code(icd10Code).orElseThrow(() -> new RuntimeException("엔티티 없음"));
        return icdApiClient.getVersionInfo("2016");


//        icdApiClient.
    }

    // 2. ICD10 질병 코드로 검색 -> ICD 11 추천(boolean) -> 부위 -> 상세 부위
    public ICD10ApiRes.CodeForReleaseIdApiRes getCodeInfoForVersion(String icd10Code) {
        ICD10Info icd10Info = icd10InfoRepository.findByIcd10Code(icd10Code).orElseThrow(() -> new RuntimeException("없는 코드"));
        log.info("icd10Info.getIcd11Uri()={}", icd10Info.getIcd11Uri());
        log.info("icd10Info.getIcd11EntityId()={}", icd10Info.getIcd11EntityId());

        // TODO :: 입력받은 icd10Code와 유사한 다른 icd10Code 리스트 (icd 엔티티에 연결된 기준)
        List<ICD10Info> icd10InfoList = icd10InfoRepository.findAllByIcd11EntityId(icd10Info.getIcd11EntityId());
        log.info("icd10InfoListFor11Entity()={}", icd10InfoList.stream().map(ICD10Info::getIcd10Title).toList());
        log.info("icd10InfoListFor11Entity()={}", icd10InfoList.stream().map(ICD10Info::getIcd10Code).toList());
        log.info("icd10InfoListFor11Entity.size()={}", icd10InfoList.size());

        //TODO :: ICD11 요청
        ICD11ApiRes.EntityRes entityInfo = icdApiClient.getEntityInfo(icd10Info.getIcd11EntityId());
        log.info("entityInf={}", entityInfo);

        //TODO :: Response 만들기

        return icdApiClient.getCodeInfoForVersion(icd10Code);
    }

    public ICD11ApiRes.EntityDetailRes getLinearizedEntityInfo(String entityId) {
        return icdApiClient.getLinearizedEntityInfo(entityId);
    }

    public ICD10ApiRes.CodeApiRes getCodeInfo(String icd10Code) {
        return icdApiClient.getCodeInfo(icd10Code);
    }

    public ICD11ApiRes.EntityRes getEntityInfo(String entityId) {
        return icdApiClient.getEntityInfo(entityId);
    }

    public void getICD11Entities() {

    }

}
