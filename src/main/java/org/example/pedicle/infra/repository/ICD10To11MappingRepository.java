package org.example.pedicle.infra.repository;

import org.example.pedicle.domain.ICD10Info;
import org.example.pedicle.domain.ICD10To11Mapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICD10To11MappingRepository extends JpaRepository<ICD10To11Mapping, Long> {

    List<ICD10To11Mapping> findAllByIcd10Code(String icd10Code);

    List<ICD10To11Mapping> findAllByIcd11EntityIdIn(List<String> icd11EntityIds);
}
