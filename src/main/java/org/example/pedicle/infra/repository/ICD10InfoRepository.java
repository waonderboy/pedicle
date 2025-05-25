package org.example.pedicle.infra.repository;

import org.example.pedicle.domain.ICD10Info;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICD10InfoRepository extends JpaRepository<ICD10Info, Long> {


    Optional<ICD10Info> findByIcd10Code(String icd10Code);

    List<ICD10Info> findAllByIcd11EntityId(String icd11EntityId);
}
