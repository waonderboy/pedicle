package org.example.pedicle.infra.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

public class ICD10ApiRes {
    //TODO :: 1. releaseId는 총 몇개?
    //TODO :: 2. 가능한 언어?

    @Data
    public static class IcdMultiVersionResponse {
        // GET icd/release/10/{code}
        // GET icd/release/10/M51.1
        // * ICD-10의 특정 질병코드에 해당하는 상세한 정보를 나타냄

        /**
         * {
         *   "@context": "http://id.who.int/icd/contexts/contextForMultiVersion.json",
         *   "@id": "http://id.who.int/icd/release/10/M51",
         *   "title": {
         *     "@language": "en",
         *     "@value": "Other intervertebral disc disorders"
         *   },
         *   "latestRelease": "http://id.who.int/icd/release/10/2019/M51",
         *   "release": [
         *     "http://id.who.int/icd/release/10/2019/M51",
         *     "http://id.who.int/icd/release/10/2016/M51",
         *     "http://id.who.int/icd/release/10/2010/M51",
         *     "http://id.who.int/icd/release/10/2008/M51"
         *   ]
         * }
         */

        private String context;
        private String id;
        private Title title;
        private String latestRelease;
        private List<String> release;
    }
    @Data
    public static class ReleaseIdApiRes {
        // GET icd/release/10/{releaseId}
        // GET icd/release/10/2019
        // * 특정 ICD 릴리스 버전 전체에 대한 정보

        @JsonProperty("@context")
        private String context;
        @JsonProperty("@id")
        private String id;
        private Title title;
        private Definition definition;
        private List<String> child;
        private String releaseDate;
        private String releaseId;
        private String browserUrl;
    }

    @Data
    public static class CodeApiRes {
        // GET icd/release/{releaseId}
        // GET icd/release/2019
        // * 특정 질병코드에 대한 정보. 코드가 개정된 이력 버젼이 전부 나옴
        /**
         * {
         *   "@context": "http://id.who.int/icd/contexts/contextForMultiVersion.json",
         *   "@id": "http://id.who.int/icd/release/10/M51.1",
         *   "title": {
         *     "@language": "en",
         *     "@value": "Lumbar and other intervertebral disc disorders with radiculopathy"
         *   },
         *   "latestRelease": "http://id.who.int/icd/release/10/2019/M51.1",
         *   "release": [
         *     "http://id.who.int/icd/release/10/2019/M51.1",
         *     "http://id.who.int/icd/release/10/2016/M51.1",
         *     "http://id.who.int/icd/release/10/2010/M51.1",
         *     "http://id.who.int/icd/release/10/2008/M51.1"
         *   ]
         * }
         */

        private LangValue title;
        private String latestRelease;
        private List<String> release;

        @Data
        public static class LangValue {
            @JsonProperty("@language")
            private String language;

            @JsonProperty("@value")
            private String value;
        }
    }

    @Data
    public static class CodeForReleaseIdApiRes {
        // GET icd/release/10/{releaseId}/{code}
        // GET icd/release/10/2019/M51.1

        private LangValue title;
        private LangValue definition;
        private LangValue longDefinition;
        private LangValue fullySpecifiedName;
        private String source;
        private String code;
        private LangValue note;
        private LangValue codingHint;
        private String classKind;
        private List<String> child;
        private List<String> parent;
        private List<IndexEntry> indexTerm;
        private List<IndexEntry> inclusion; //이 코드에 포함되는 질환 또는 표현 - 확장되는 개념
        private List<IndexEntry> exclusion; //이 코드에는 포함되지 않는, 유사하지만 별도 코드로 처리해야 하는 질환 - 헷갈리는 경우 방지
        private String browserUrl;

        @Data
        public static class LangValue {
            @JsonProperty("@language")
            private String language;

            @JsonProperty("@value")
            private String value;
        }

        @Data
        public static class IndexEntry {
            private LangValue label;
            private String foundationReference;
            private String linearizationReference;
            private boolean deprecated;
        }
    }

    @Data
    public static class Definition {
        @JsonProperty("@language")
        private String language;

        @JsonProperty("@value")
        private String value;
    }

    @Data
    public static class DetailRes {
        // GET icd/release/{releaseId}
        // GET icd/release/10/2019
        // * 특정 ICD 릴리스 버전 전체에 대한 정보
        //TODO: inclusion exclusion 차이

        @JsonProperty("@context")
        private String context;
        @JsonProperty("@id")
        private String id;

        private String browserUrl;
        private String code;
        private Title title;
        private String usage;
        private String classKind;
        private List<String> parent;
        private List<String> child;
        private List<LabelWrapper> inclusion; // TODO 뭔지 찾아보기!
        private List<LabelWrapper> exclusion; // TODO 뭔지 찾아보기!

        public List<String> getInclusionLabels() {
            return inclusion.stream().map(i -> i.getLabel().getValue()).toList();
        }

        public List<String> getExclusionLabels() {
            return exclusion.stream().map(e -> e.getLabel().getValue()).toList();
        }

        @Data
        public static class LabelWrapper {
            private Label label;
        }
    }

    @Data
    public static class Label {
        @JsonProperty("@language")
        private String language;
        @JsonProperty("@value")
        private String value;
    }

    @Data
    public static class Title {
        @JsonProperty("@language")
        private String language;
        @JsonProperty("@value")
        private String value;
    }
}
