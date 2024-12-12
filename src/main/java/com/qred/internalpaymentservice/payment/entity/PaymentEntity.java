package com.qred.internalpaymentservice.payment.entity;

import com.qred.internalpaymentservice.contract.entity.ContractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a Payment entity that is persisted in the database. A Payment stores information about
 * the payment date, payment amount, payment type And the associated contract the payment belongs
 * to.
 *
 * @author georgemore on 2024-12-10
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
@SequenceGenerator(
    name = "payment_seq_generator",
    sequenceName = "payment_seq",
    allocationSize = 50)
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq_generator")
  private Long id;

  @Column(name = "payment_date", nullable = false)
  private LocalDate paymentDate;

  @Column(name = "amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_type", nullable = false)
  private PaymentType paymentType;

  @ManyToOne
  @JoinColumn(name = "contract_id")
  private ContractEntity contractEntity;

  public PaymentEntity setPaymentDate(LocalDate paymentDate) {
    this.paymentDate = paymentDate;
    return this;
  }

  public PaymentEntity setAmount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public PaymentEntity setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
    return this;
  }

  public PaymentEntity setContractEntity(ContractEntity contractEntity) {
    this.contractEntity = contractEntity;
    return this;
  }
}
