package org.example.pedicle.domain;

import lombok.*;
import org.example.pedicle.infra.client.dto.ICD11ApiRes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ICDInfo {

    @Getter
    @Builder
    @ToString
    public static class Icd11Main {
        private String entityId;
        private String title;
        private String definition;

        private List<Exclusion> exclusions;
        private List<ICDInfo.CoordinationOption> coordinationOptions;

        public static Icd11Main from(ICD10To11Mapping entity, ICD11ApiRes.EntityDetailRes result, List<ICDInfo.CoordinationOption> coordinationOptions) {
            return Icd11Main.builder()
                    .entityId(entity.getIcd11EntityId())
                    .title(entity.getIcd11Title())
                    .definition(Optional.ofNullable(result.getDefinition())
                            .map(ICD11ApiRes.EntityDetailRes.LocalizedString::getValue)
                            .orElse(null))
                    .exclusions(Optional.ofNullable(result.getExclusion())
                            .map(en -> en.stream().map(Exclusion::from).toList())
                            .orElse(null))
                    .coordinationOptions(coordinationOptions)
                    .build();
        }

        public Icd11Main include(List<ICDInfo.CoordinationOption> coordinationOptions) {
            this.coordinationOptions = coordinationOptions;
            return this;
        }
    }

    @Getter
    @Builder
    @ToString
    public static class CoordinationOption {
        private String type;
        private boolean required;
        private List<OptionElement> elements;

        public static CoordinationOption from(String axisName, String required) {
            return CoordinationOption.builder()
                    .type(axisName.split("/")[axisName.split("/").length - 1])
                    .required(Boolean.parseBoolean(required))
                    .build();
        }

        public CoordinationOption include(List<OptionElement> optionElements) {
            this.elements = optionElements;
            return this;
        }
    }

    @Getter
    @Builder
    @ToString
    public static class OptionElement {
        private String entityId;
        private String title;

        public static OptionElement from(ICD11ApiRes.EntityDetailRes info) {
            return OptionElement.builder()
                    .entityId(info.getSource().split("/")[info.getSource().split("/").length - 1])
                    .title(info.getTitle().getValue())
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class Exclusion {
        private String entityId;
        private String title;

        public static Exclusion from(ICD11ApiRes.EntityDetailRes.TermEntry entry) {
            return Exclusion.builder()
                    .entityId(entry.getFoundationReference().split("/")[entry.getFoundationReference().split("/").length -1])
                    .title(Optional.ofNullable(entry.getLabel()).map(ICD11ApiRes.EntityRes.LocalizedString::getValue).orElse(null))
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class SimilarDisease {
        private String currentIcd10Code;
        private String currentTitle;

        private List<CandidateDisease> candidates;

        private List<DiseaseCode> simpleSimilarCode;
        private List<DiseaseCode> clinicalSimilarCode;

        public static SimilarDisease from(String currentIcd10Code,
                                    String currentTitle,
                                    List<ICD10To11Mapping> candidates,
                                    List<ICD10To11Mapping> anatomy,
                                    List<ICD10Info> statistic) {
            return SimilarDisease.builder()
                    .currentIcd10Code(currentIcd10Code)
                    .currentTitle(currentTitle)
                    .candidates(Optional.ofNullable(candidates)
                            .map(c -> c.stream()
                                    .map(CandidateDisease::from)
                                    .collect(Collectors.toList()))
                            .orElse(null))
                    .clinicalSimilarCode(Optional.ofNullable(anatomy)
                            .map(an -> an.stream().map(DiseaseCode::from)
                                    .collect(Collectors.toSet()).stream().sorted(Comparator.comparing(DiseaseCode::getIcd10Code)).toList()
                            )
                            .orElse(null))
                    .simpleSimilarCode(Optional.ofNullable(statistic)
                            .map(st -> st.stream().map(DiseaseCode::from).sorted(Comparator.comparing(DiseaseCode::getIcd10Code)).toList())
                            .orElse(null))
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode(of = {"icd10Code", "title"})
    public static class DiseaseCode {
        private String icd10Code;
        private String title;

        public static DiseaseCode from(ICD10To11Mapping entity) {
            return DiseaseCode.builder()
                    .icd10Code(entity.getIcd10Code())
                    .title(entity.getIcd10Title())
                    .build();
        }

        public static DiseaseCode from(ICD10Info entity) {
            return DiseaseCode.builder()
                    .icd10Code(entity.getIcd10Code())
                    .title(entity.getIcd10Title())
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode(of = "icd11EntityId")
    public static class CandidateDisease {
        private String icd11EntityId;
        private String title;

        public static CandidateDisease from(ICD10To11Mapping entity) {
            return CandidateDisease.builder()
                    .icd11EntityId(entity.getIcd11EntityId())
                    .title(entity.getIcd11Title())
                    .build();
        }
    }

}
