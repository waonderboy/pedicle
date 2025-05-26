package org.example.pedicle.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor
@Table(name = "icd10_to_11_mapping")
public class ICD10To11Mapping {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "icd10_to_11_mapping_id")
    private Long id;
    private Integer icd10DepthInKind;
    @Enumerated(EnumType.STRING)
    private ClassKind icd10ClassKind;
    private String icd10Code;
    private String icd10Chapter;
    @Column(columnDefinition = "text")
    private String icd10Title;

//    private String icd10CustomTitle; // 원장님께서 입력한 타이틀
//    private String description;
//    private String abbreviation; //약자


    private Integer icd11DepthInKind;
    @Enumerated(EnumType.STRING)
    private ClassKind icd11ClassKind;
    private String icd11EntityId;
    @Column(columnDefinition = "text")
    private String icd11FoundationUri;
    @Column(columnDefinition = "text")
    private String icd11LinearizationUri;
    @Column(columnDefinition = "text")
    private String icd11Title;
    private String icd11Code;
    private String icd11Chapter;

    @Builder
    public ICD10To11Mapping(Integer icd10DepthInKind,
                            ClassKind icd10ClassKind,
                            String icd10Code,
                            String icd10Chapter,
                            String icd10Title,
                            Integer icd11DepthInKind,
                            ClassKind icd11ClassKind,
                            String icd11EntityId,
                            String icd11FoundationUri,
                            String icd11LinearizationUri,
                            String icd11Title,
                            String icd11Code,
                            String icd11Chapter) {
        this.icd10DepthInKind = icd10DepthInKind;
        this.icd10ClassKind = icd10ClassKind;
        this.icd10Code = icd10Code;
        this.icd10Chapter = icd10Chapter;
        this.icd10Title = icd10Title;
        this.icd11DepthInKind = icd11DepthInKind;
        this.icd11ClassKind = icd11ClassKind;
        this.icd11EntityId = icd11EntityId;
        this.icd11FoundationUri = icd11FoundationUri;
        this.icd11LinearizationUri = icd11LinearizationUri;
        this.icd11Title = icd11Title;
        this.icd11Code = icd11Code;
        this.icd11Chapter = icd11Chapter;
    }

    @Getter
    @AllArgsConstructor
    public enum RelationType {
        SUBCLASS("하위 개념 존재"), EQUIVALENT("동일 개념");
        private final String description;
    }

    @Getter
    @AllArgsConstructor
    public enum ClassKind {
        //카테고리 - 서브카테고리 존재
        CATEGORY("구체적 분류"), BLOCK("중간 분류"), CHAPTER("최상위 분류");
        private final String description;
    }

}
