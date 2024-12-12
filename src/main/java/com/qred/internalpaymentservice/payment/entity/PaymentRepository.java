package com.qred.internalpaymentservice.payment.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * JPA Repository for the Payment Entity.
 *
 * @author georgemore on 2024-12-10
 */
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

  @Query("select pe from PaymentEntity pe where pe.contractEntity.contractNumber = :contractNumber")
  List<PaymentEntity> findByContractNumber(String contractNumber);
}
