package com.qred.internalpaymentservice.contract.entity;

import com.qred.internalpaymentservice.client.ClientEntity;
import com.qred.internalpaymentservice.payment.entity.PaymentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a contract entity that is persisted in the database. A contract is associated with a
 * specific client and has an unique contract number. Additionally, it has a one-to-many
 * relationship with payments, where each payment is linked to this contract.
 *
 * @author georgemore on 2024-12-10
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "contract")
@NoArgsConstructor
public class ContractEntity {

  @Id private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private ClientEntity clientEntity;

  @Column(name = "contract_number", nullable = false, unique = true)
  private String contractNumber;

  @OneToMany(mappedBy = "contractEntity", fetch = FetchType.LAZY)
  private List<PaymentEntity> paymentEntities = new ArrayList<>();
}
