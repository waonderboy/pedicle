package org.example.pedicle.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor
@Table(name = "ICD10_info")
public class ICD10Info {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ICD10_info_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private ICD10ClassKind icd10ClassKind;
    private Integer icd10DepthInKind;
    private String icd10Code;
    private String icd10Chapter;
    @Column(columnDefinition = "text")
    private String icd10Title;

    private String icd10CustomTitle; // 원장님께서 입력한 타이틀
    private String description;
    private String abbreviation; //약자


    private String icd11EntityId;
    @Column(columnDefinition = "text")
    private String icd11Uri;
    @Column(columnDefinition = "text")
    private String icd11Title;
    @Enumerated(EnumType.STRING)
    private RelationType relationType;

    @Builder
    public ICD10Info(ICD10ClassKind icd10ClassKind,
                     Integer icd10DepthInKind,
                     String icd10Code,
                     String icd10Chapter,
                     String icd10Title,
                     String icd11EntityId,
                     String icd11Uri,
                     String icd11Title,
                     RelationType relationType) {
        this.icd10ClassKind = icd10ClassKind;
        this.icd10DepthInKind = icd10DepthInKind;
        this.icd10Code = icd10Code;
        this.icd10Chapter = icd10Chapter;
        this.icd10Title = icd10Title;
        this.icd11EntityId = icd11EntityId;
        this.icd11Uri = icd11Uri;
        this.icd11Title = icd11Title;
        this.relationType = relationType;
    }

    @Getter
    @AllArgsConstructor
    public enum RelationType {
        SUBCLASS("하위 개념 존재"), EQUIVALENT("동일 개념");
        private final String description;
    }

    @Getter
    @AllArgsConstructor
    public enum ICD10ClassKind {
        //카테고리 - 서브카테고리 존재
        CATEGORY("구체적 분류"), BLOCK("중간 분류"), CHAPTER("최상위 분류");
        private final String description;
    }

}
