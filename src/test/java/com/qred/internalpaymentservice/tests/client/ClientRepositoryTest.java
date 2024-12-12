package com.qred.internalpaymentservice.tests.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.qred.internalpaymentservice.client.ClientDetails;
import com.qred.internalpaymentservice.client.ClientEntity;
import com.qred.internalpaymentservice.client.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * @author georgemore on 2024-12-11
 */
@DataJpaTest
class ClientRepositoryTest {

  @Autowired private ClientRepository clientRepository;

  @Test
  void testSaveAndFindClient() {
    ClientDetails clientDetails = new ClientDetails("John", "Doe");
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setClientDetails(clientDetails);
    clientEntity.setId(1L);

    ClientEntity savedEntity = clientRepository.save(clientEntity);

    // assert
    assertEquals(1L, savedEntity.getId());
    assertEquals("John", savedEntity.getClientDetails().getFirstName());
    assertEquals("Doe", savedEntity.getClientDetails().getLastName());
  }
}
