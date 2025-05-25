package org.example.pedicle.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor
public class ICD10SimilarCodeInfo {

    /**
     * - 기본적으로 같은 카테고리에 속한 코드는 유사한 코드로 판단 (한 부모의 자식들)
     * - 그 외 해당 테이블은 의사 개개인이 판단하기에 유사한 코드로 취급 (커스텀 SimilarCode)
     * - icd10InfoId1와 icd10InfoId2는 순서 관계없이 복합키로 취급
     *   ex) (M511, M512) 와 (M512, M511) 동일하게 취급
     */

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ICD10_info_id_1")
    private ICD10Info icd10InfoId1;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ICD10_info_id_2")
    private ICD10Info icd10InfoId2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    //TODO 정렬하여 저장 icd10InfoId1 < icd10InfoId2


}
