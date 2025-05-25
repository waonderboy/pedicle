package org.example.pedicle.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity @Getter
@NoArgsConstructor
public class Diagnosis {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_id")
    private Long id;

    private String title; // 원장님께서 입력한 타이틀
    private String abbreviation; //약자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ICD10_info_id")
    private ICD10Info icd10Info;

    // TODO: 부위 - 필드 추가

    // TODO: 세부 부위 - 필드 추가

    @Getter
    @AllArgsConstructor
    public enum Type {
        DISEASE("질병"), INJURY("상해");
        private final String description;
    }
}
