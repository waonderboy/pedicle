package org.example.pedicle.infra.client;


public class ICDUri {
    public static final String ICD10_VERSION = "https://id.who.int/icd/release/10/{releaseId}";
    public static final String ICD10_CODE_INFO = "https://id.who.int/icd/release/10/{code}";
    public static final String ICD10_CODE_INFO_FOR_VERSION = "https://id.who.int/icd/release/10/{releaseId}/{code}";
    public static final String TOKEN_URL = "https://icdaccessmanagement.who.int/connect/token";

    public static final String ICD11_ENTITY_INFO = "https://id.who.int/icd/entity/{entityId}";

    public static final String ICD11_LINEARIZATION_INFO = "https://id.who.int/icd/release/11/{releaseId}/{linearizationName}/{entityId}";



}
