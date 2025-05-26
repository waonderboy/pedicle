package org.example.pedicle.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedicle.infra.client.ICDApiClient;
import org.example.pedicle.infra.client.dto.ICD10ApiRes;
import org.example.pedicle.infra.client.dto.ICD11ApiRes;
import org.example.pedicle.infra.repository.ICD10InfoRepository;
import org.example.pedicle.infra.repository.ICD10To11MappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisService {
    private final ICDApiClient icdApiClient;
    private final ICD10InfoRepository icd10InfoRepository;
    private final ICD10To11MappingRepository icd10To11MappingRepository;

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
        ICD11ApiRes.EntityRes entityInfo = icdApiClient.getFoundationInfo(icd10Info.getIcd11EntityId());
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
        return icdApiClient.getFoundationInfo(entityId);
    }

    public void getSimilar10CodeBy(String icd10Code) {
        List<ICD10To11Mapping> allByIcd10Code = icd10To11MappingRepository.findAllByIcd10Code(icd10Code);

        // TODO :: 입력받은 icd10Code와 유사한 다른 icd10Code 리스트 (icd 엔티티에 연결된 기준)
        List<String> icd11EntityIds = allByIcd10Code.stream().map(ICD10To11Mapping::getIcd11EntityId).toList();
        List<ICD10To11Mapping> allByIcd11EntityIdIn = icd10To11MappingRepository.findAllByIcd11EntityIdIn(icd11EntityIds);


        ICD10Info icd10Info = icd10InfoRepository.findByIcd10Code(icd10Code).orElseThrow(() -> new RuntimeException("없는 코드"));
        List<ICD10Info> icd10InfoList = icd10InfoRepository.findAllByIcd11EntityId(icd10Info.getIcd11EntityId());

        //통계를 위한 단일 카테고리
        log.info("Similar 10 Code From Multi Categories = {}", allByIcd11EntityIdIn.stream().map(ICD10To11Mapping::getIcd10Code).collect(Collectors.toSet()));
        //여러 조합으로 분해됨
        log.info("Similar 10 Code From Single Categories = {}", icd10InfoList.stream().map(ICD10Info::getIcd10Code).collect(Collectors.toSet()));

        // TODO :: 입력받은 icd10Code와 유사한 다른 icd10Code 리스트 (icd 엔티티에 연결된 기준)
//        Set<String> foundationUris = allByIcd11EntityIdIn.stream().map(ICD10To11Mapping::getIcd11FoundationUri).collect(Collectors.toSet());
//        Set<String> linearizedUris = allByIcd11EntityIdIn.stream().map(ICD10To11Mapping::getIcd11LinearizationUri).collect(Collectors.toSet());

        Set<String> multiCategoryEntityIds = allByIcd11EntityIdIn.stream().map(ICD10To11Mapping::getIcd11EntityId).collect(Collectors.toSet());
        Set<String> singleCategoryEntityIds = icd10InfoList.stream().map(ICD10Info::getIcd11EntityId).collect(Collectors.toSet());

        log.info("multiCategoryEntityIds = {}", multiCategoryEntityIds);
        log.info("singleCategoryEntityIds = {}", singleCategoryEntityIds);

        // 멀티 카테고리
        ICD11ApiRes.EntityDetailRes linearizedEntityInfo = icdApiClient.getLinearizedEntityInfo(multiCategoryEntityIds.stream().toList().get(0));
        icdApiClient.getFoundationInfo(multiCategoryEntityIds.stream().toList().get(0));

        linearizedEntityInfo.getPostcoordinationScale().forEach(post -> {
//            post.getAxisName()
            checkPostType(post);
            List<String> scaleEntity = post.getScaleEntity();
            if (scaleEntity != null && !scaleEntity.isEmpty()) {
                log.info("entity={}",scaleEntity.get(0).split("/")[scaleEntity.get(0).split("/").length -1]);
                icdApiClient.getLinearizedEntityInfo(scaleEntity.get(0).split("/")[scaleEntity.get(0).split("/").length -1]);
//                icdApiClient.getLinearizedEntityInfoByUri(scaleEntity.get(0));
            }
        });


    }

    public void getICD11Entities(String icd10Code) {


    }

    public ICDInfo.SimilarDisease getIcd10SimilarWith(String icd10Code) {
        List<ICD10To11Mapping> allByIcd10Code = icd10To11MappingRepository.findAllByIcd10Code(icd10Code);

        if(allByIcd10Code.isEmpty()) {
            return null;
        }

        List<String> icd11EntityIds = allByIcd10Code.stream().map(ICD10To11Mapping::getIcd11EntityId).toList();
        // anatomy
        List<ICD10To11Mapping> anatomies = icd10To11MappingRepository.findAllByIcd11EntityIdIn(icd11EntityIds);
        ICD10Info icd10Info = icd10InfoRepository.findByIcd10Code(icd10Code).orElseThrow(() -> new RuntimeException("없는 코드"));
        // 통계
        List<ICD10Info> stats = icd10InfoRepository.findAllByIcd11EntityId(icd10Info.getIcd11EntityId());

        //여러 조합으로 분해
        log.info("Similar 10 Code From Multi Categories = {}", anatomies.stream().map(ICD10To11Mapping::getIcd10Code).collect(Collectors.toSet()));
        //통계를 위한 단일 카테고리
        log.info("Similar 10 Code From Single Categories = {}", stats.stream().map(ICD10Info::getIcd10Code).collect(Collectors.toSet()));


        return ICDInfo.SimilarDisease.from(
                allByIcd10Code.get(0).getIcd10Code(),
                allByIcd10Code.get(0).getIcd10Title(),
                allByIcd10Code.stream().sorted(Comparator.comparing(ICD10To11Mapping::getIcd10Code)).toList(),
                anatomies.stream().sorted(Comparator.comparing(ICD10To11Mapping::getIcd10Code)).toList(),
                stats.stream().sorted(Comparator.comparing(ICD10Info::getIcd10Code)).toList()
        );
    }



    private void checkPostType(ICD11ApiRes.EntityDetailRes.PostcoordinationScale scale){
        String[] splitSchemaUri = scale.getAxisName().split("/");
        String postcoordinationScaleType = splitSchemaUri[splitSchemaUri.length - 1];

        switch (postcoordinationScaleType) {
            case "specificAnatomy" -> log.info("specificAnatomy");
            case "hasManifestation" -> log.info("hasManifestation");
            case "hasCausingCondition" -> log.info("hasCausingCondition");
        }

    }

    public ICDInfo.Icd11Main getIcd11Info(String entityId) {
        List<ICD10To11Mapping> allByIcd11EntityIdIn = icd10To11MappingRepository.findAllByIcd11EntityIdIn(List.of(entityId));
        if (allByIcd11EntityIdIn.isEmpty()) {
            throw new RuntimeException("엔티티 없음");
        }
        ICD11ApiRes.EntityDetailRes linearizedEntityInfo = icdApiClient.getLinearizedEntityInfo(entityId);

        List<ICD11ApiRes.EntityDetailRes.PostcoordinationScale> postcoordinationScale = linearizedEntityInfo.getPostcoordinationScale();

        List<ICDInfo.CoordinationOption> options = new ArrayList<>();

        if (postcoordinationScale != null) {

            for (ICD11ApiRes.EntityDetailRes.PostcoordinationScale post : postcoordinationScale) {//                options.add(ICDInfo.CoordinationOption.builder()
//                        .type(post.getAxisName().split("/")[post.getAxisName().split("/").length - 1])
//                        .required(Boolean.parseBoolean(post.getRequiredPostcoordination()))
//                        .build());

                //
                List<String> scaleEntity = post.getScaleEntity();
                ICDInfo.CoordinationOption coorOption = ICDInfo.CoordinationOption.from(post.getAxisName(), post.getRequiredPostcoordination());

                if (scaleEntity != null && !scaleEntity.isEmpty()) {
                    log.info("entity={}", scaleEntity.get(0).split("/")[scaleEntity.get(0).split("/").length - 1]);

                    //TODO 자식가져오기
                    ICD11ApiRes.EntityDetailRes scaleEntityInfos = icdApiClient.getLinearizedEntityInfo(scaleEntity.get(0).split("/")[scaleEntity.get(0).split("/").length - 1]);

                    options.add(getChildInfo(coorOption, scaleEntityInfos));
                    break;
                }
            }
        }


        return ICDInfo.Icd11Main.from(allByIcd11EntityIdIn.get(0), linearizedEntityInfo, options);
    }

    public ICDInfo.CoordinationOption getChildInfo(ICDInfo.CoordinationOption coord, ICD11ApiRes.EntityDetailRes scaleEntityInfos) {
        if (scaleEntityInfos.getChild() == null || scaleEntityInfos.getChild().isEmpty()) {
            return coord;
        }

        List<ICDInfo.OptionElement> elements = new ArrayList<>();

        scaleEntityInfos.getChild().forEach(child -> {
            String childId = child.split("/")[child.split("/").length - 1];
            elements.add(ICDInfo.OptionElement.from(icdApiClient.getLinearizedEntityInfo(childId)));
        });

        return coord.include(elements);
    }
}
