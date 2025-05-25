package org.example.pedicle.batch;

import lombok.Data;

@Data
public class ICD10MappingCSV {
//    icd10ClassKind,icd10DepthInKind,icd10Code,icd10Chapter,icd10Title,icd11Uri,icd11Title,relationType,
    private String icd10ClassKind;
    private Integer icd10DepthInKind;
    private String icd10Code;
    private String icd10Chapter;
    private String icd10Title;
    private String icd11Uri;
    private String icd11Title;
    private String relationType;
}
