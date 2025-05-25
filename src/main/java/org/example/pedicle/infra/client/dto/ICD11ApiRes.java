package org.example.pedicle.infra.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

public class ICD11ApiRes {

    @Data
    public static class IcdSearch {
        // GET icd/entity/search

        private List<DestinationEntity> destinationEntities; // 검색 결과 엔티티 리스트
        private boolean error; // 에러 발생 여부
        private String errorMessage; // 에러 메시지
        private boolean resultChopped; // 결과가 너무 많아 일부만 반환되었는지 여부
        private boolean wordSuggestionsChopped; // 단어 제안 결과가 잘렸는지 여부
        private int guessType; // 추정된 검색 방식 유형 (0: 정확, 1: 유사어, 2: 오타 보정 등)
        private String uniqueSearchId; // 이 검색 요청에 대한 고유 ID
        private List<Word> words; // 검색된 단어 또는 제안 단어 리스트 useFlexisearch(true) 시 반환됨

        @Data
        public static class DestinationEntity {
            private String id; // 현재 엔티티의 고유 ID (URI)
            private String title; // 검색 결과 제목 (진단명 등) ex) "Central <em class='found'>fever</em>" ---- <em> 은 하이라이팅
            private String stemId; // Foundation Layer 상의 핵심 개념 ID
            private boolean isLeaf; // 리프 노드 여부 (하위 항목이 없는 최종 진단 항목)
            private int postcoordinationAvailability; // 사후 조합(Postcoordination) 가능 여부 (0: 불가능)
            private boolean hasCodingNote; // 코딩 주석이 존재하는지 여부
            private boolean hasMaternalChapterLink; // 모성 챕터와 연결되어 있는지 - (임신, 출산 및 산후 기간 중에 발생하는 질병과 상태들을 분류)
            private boolean hasPerinatalChapterLink; // 주산기 챕터와 연결되어 있는지 - (출생 전후(주산기, perinatal)에 발생하는 신생아의 질환)
            private List<MatchingPV> matchingPVs; // 현재 엔티티와 동의어. 속성 일치 결과 목록 (검색 점수 등 포함)
            private boolean propertiesTruncated; // 속성이 너무 많아 잘렸는지 여부
            private boolean isResidualOther; // '기타(Other)' 항목인지 여부
            private boolean isResidualUnspecified; // '상세불명(Unspecified)' 항목인지 여부
            private String chapter; // 속한 챕터 이름
            private String theCode; // MMS에서의 ICD 코드 (예: 5A11)
            private double score; // 검색 유사도 점수
            private boolean titleIsASearchResult; // 제목이 검색어와 일치하는지 여부
            private boolean titleIsTopScore; // 이 항목이 가장 높은 점수인지 여부
            private int entityType; // 엔티티 타입 (내부 분류용)
            private boolean important; // 사용자에게 중요한 항목으로 간주되는지 여부

            private List<String> descendants; // 이 항목의 자식 개념 ID 목록
        }

        @Data
        public static class MatchingPV {
            private String propertyId; // 속성 ID
            private String label; // 속성 라벨 (예: Synonym 등)
            private double score; // 속성 일치 점수 0-1
            private boolean important; // 중요한 속성인지 여부
            private String foundationUri; // Foundation URI
            private int propertyValueType; // 속성 타입 코드 (예: 0: 기본, 1: synonym 등)
        }

        @Data
        public static class Word {
            private String label; // 제안된 단어 또는 실제 검색어
            private boolean dontChangeResult; // 결과 변경에 영향을 주지 않는 보조 단어 여부
        }
    }

    @Data
    @ToString
    public static class EntityRes {

        private LocalizedString title;
        private LocalizedString definition;
        private LocalizedString longDefinition;
        private LocalizedString fullySpecifiedName;
        private LocalizedString diagnosticCriteria;

        private List<String> child;
        private List<String> parent;
        private List<String> ancestor;
        private List<String> descendant;

        private List<TermEntry> synonym;
        private List<TermEntry> narrowerTerm;
        private List<TermEntry> inclusion;
        private List<TermEntry> exclusion;

        private String browserUrl;

        @Data
        @ToString
        public static class LocalizedString {

            @JsonProperty("@language")
            private String language;

            @JsonProperty("@value")
            private String value;
        }

        @Data
        @ToString
        public static class TermEntry {

            private LocalizedString label;
            private String foundationReference;
            private String linearizationReference;
            private boolean deprecated;
        }
    }

    @Data
    public static  class EntityDetailRes {
        private String context;
        private String id;

        private LocalizedString title;
        private LocalizedString definition;
        private LocalizedString longDefinition;
        private LocalizedString fullySpecifiedName;
        private LocalizedString diagnosticCriteria;
        private LocalizedString codingNote;

        private String source;
        private String code;
        private String blockId;
        private String codeRange;
        private String classKind;

        private List<String> child;
        private List<String> parent;
        private List<String> ancestor;
        private List<String> descendant;
        private List<String> relatedEntitiesInMaternalChapter;
        private List<String> relatedEntitiesInPerinatalChapter;

        private List<TermEntry> foundationChildElsewhere;
        private List<TermEntry> indexTerm;
        private List<TermEntry> inclusion;
        private List<TermEntry> exclusion;

        private List<PostcoordinationScale> postcoordinationScale; // 어떤 개념(질병 코드)이 추가적으로 조합 가능한 속성(예: 해부학적 위치, 중증도, 원인 등)

        private String browserUrl;
        @Data
        @ToString
        public static class LocalizedString {

            @JsonProperty("@language")
            private String language;

            @JsonProperty("@value")
            private String value;
        }

        @Data
        @ToString
        public static class TermEntry {

            private EntityRes.LocalizedString label;
            private String foundationReference;
            private String linearizationReference;
            private boolean deprecated;
        }


        @Data
        public static class PostcoordinationScale {
            private String axisName;
            private String requiredPostcoordination;
            private String allowMultipleValues;
            private List<String> scaleEntity;
        }
    }

    /** Types of PostcoordinationScale
     * Associated with 관련된 정보
     * Causing condition 발생 조건
     * Has manifestation 증상
     * Specific anatomy 특정 해부학적 부위
     * Infectious agents 감염원
     * Chemical agents 화학제
     * Medication 약물
     */
}
