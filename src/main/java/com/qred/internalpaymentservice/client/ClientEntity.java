package com.qred.internalpaymentservice.client;

import com.qred.internalpaymentservice.contract.entity.ContractEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a client entity that is persisted in the database. A client stores information about
 * its details and associated contracts entities.
 *
 * @author georgemore on 2024-12-10
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "client")
public class ClientEntity {

  @Id private Long id;

  @Embedded private ClientDetails clientDetails;

  @OneToMany(mappedBy = "clientEntity", fetch = FetchType.LAZY)
  private List<ContractEntity> contractEntities = new ArrayList<>();
}
