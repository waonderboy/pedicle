package org.example.pedicle.batch;

import lombok.Data;

@Data
public class ICD10MappingOneToManyCSV {
    private String icd10ClassKind;
    private Integer icd10DepthInKind;
    private String icd10Code;
    private String icd10Chapter;
    private String icd10Title;
    private String icd11ClassKind;
    private Integer icd11DepthInKind;
    private String icd11FoundationUri;
    private String icd11LinearizationUri;
    private String icd11Code;
    private String icd11Chapter;
    private String icd11Title;
}
