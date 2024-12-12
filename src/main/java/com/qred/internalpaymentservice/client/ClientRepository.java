package com.qred.internalpaymentservice.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for the Client Entity.
 *
 * @author georgemore on 2024-12-10
 */
@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {}
