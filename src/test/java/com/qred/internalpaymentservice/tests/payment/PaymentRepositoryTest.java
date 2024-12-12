package com.qred.internalpaymentservice.tests.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.qred.internalpaymentservice.client.ClientDetails;
import com.qred.internalpaymentservice.client.ClientEntity;
import com.qred.internalpaymentservice.client.ClientRepository;
import com.qred.internalpaymentservice.contract.entity.ContractEntity;
import com.qred.internalpaymentservice.contract.entity.ContractRepository;
import com.qred.internalpaymentservice.payment.entity.PaymentEntity;
import com.qred.internalpaymentservice.payment.entity.PaymentRepository;
import com.qred.internalpaymentservice.payment.entity.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * @author georgemore on 2024-12-11
 */
@DataJpaTest
class PaymentRepositoryTest {

  @Autowired private PaymentRepository paymentRepository;

  @Autowired private ContractRepository contractRepository;

  @Autowired private ClientRepository clientRepository;

  @Test
  void testSaveAndFindPayment() {
    // save client
    ClientDetails clientDetails = new ClientDetails("John", "Doe");
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setClientDetails(clientDetails);
    clientEntity.setId(1L);
    ClientEntity savedClientEntity = clientRepository.save(clientEntity);

    // save contract entity
    ContractEntity contractEntity =
        new ContractEntity(2L, savedClientEntity, "12345", new ArrayList<>());
    ContractEntity savedContractEntity = contractRepository.save(contractEntity);

    // save payment entity
    PaymentEntity paymentEntity =
        new PaymentEntity(
            null,
            LocalDate.now(),
            new BigDecimal("1000.00"),
            PaymentType.INCOMING,
            savedContractEntity);
    PaymentEntity savedEntity = paymentRepository.save(paymentEntity);

    // assert
    assertNotNull(savedEntity.getId());
    assertEquals(paymentEntity.getAmount(), savedEntity.getAmount());
    assertEquals(
        paymentEntity.getContractEntity().getId(), savedEntity.getContractEntity().getId());
    assertEquals(
        paymentEntity.getContractEntity().getContractNumber(),
        savedEntity.getContractEntity().getContractNumber());
  }
}
