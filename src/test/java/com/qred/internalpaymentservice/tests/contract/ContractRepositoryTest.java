package com.qred.internalpaymentservice.tests.contract;

import com.qred.internalpaymentservice.client.ClientDetails;
import com.qred.internalpaymentservice.client.ClientEntity;
import com.qred.internalpaymentservice.client.ClientRepository;
import com.qred.internalpaymentservice.contract.entity.ContractEntity;
import com.qred.internalpaymentservice.contract.entity.ContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author georgemore on 2024-12-11
 */
@DataJpaTest
class ContractRepositoryTest {

  @Autowired private ContractRepository contractRepository;

  @Autowired private ClientRepository clientRepository;

  @Test
  void testSaveAndFindContract() {
    // save client entity
    ClientDetails clientDetails = new ClientDetails("John", "Doe");
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setClientDetails(clientDetails);
    clientEntity.setId(1L);
    ClientEntity savedClientEntity = clientRepository.save(clientEntity);

    // save contract entity
    ContractEntity contractEntity =
        new ContractEntity(2L, savedClientEntity, "12345", new ArrayList<>());
    ContractEntity savedContractEntity = contractRepository.save(contractEntity);

    // assert
    assertEquals(savedContractEntity.getId(), 2L);
    assertEquals("12345", savedContractEntity.getContractNumber());
    assertEquals(clientEntity.getId(), savedContractEntity.getClientEntity().getId());
  }

  @Test
  void testFindContractsByContractNumber() {
    // save client
    ClientDetails clientDetails = new ClientDetails("John", "Doe");
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setClientDetails(clientDetails);
    clientEntity.setId(1L);
    ClientEntity savedClientEntity = clientRepository.save(clientEntity);

    String firstContractNumber = "1111";
    ContractEntity firstContractEntity =
        new ContractEntity(2L, savedClientEntity, firstContractNumber, new ArrayList<>());

    String secondContractNumber = "9999";
    ContractEntity secondContractEntity =
        new ContractEntity(3L, savedClientEntity, secondContractNumber, new ArrayList<>());

    // save contracts
    contractRepository.save(firstContractEntity);
    contractRepository.save(secondContractEntity);

    // assert
    List<ContractEntity> result =
        contractRepository.findByContractNumbersIn(List.of(firstContractNumber));
    assertEquals(1, result.size());
    assertEquals(firstContractNumber, result.get(0).getContractNumber());

    // assert
    result = contractRepository.findByContractNumbersIn(List.of(secondContractNumber));
    assertEquals(1, result.size());
    assertEquals(secondContractNumber, result.get(0).getContractNumber());

    // assert
    result = contractRepository.findByContractNumbersIn(List.of(firstContractNumber, secondContractNumber));
    assertEquals(2, result.size());

    // assert
    result = contractRepository.findByContractNumbersIn(List.of("unknown id"));
    assertEquals(0, result.size());
  }
}
