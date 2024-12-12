package com.qred.internalpaymentservice.contract.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for the Contract Entity.
 *
 * @author georgemore on 2024-12-10
 */
@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, Long> {

  @Query("select ce from ContractEntity ce where ce.contractNumber = :contractNumber")
  Optional<ContractEntity> findByContractNumber(String contractNumber);

  @Query("select ce from ContractEntity ce where ce.contractNumber in :contractNumbers")
  List<ContractEntity> findByContractNumbersIn(List<String> contractNumbers);
}
