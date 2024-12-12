package com.qred.internalpaymentservice.contract.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a given contract does not exist.
 *
 * @author georgemore on 2024-12-10
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContractNotFoundException extends RuntimeException {

  public ContractNotFoundException(String contractNumber) {
    super("Contract with contractNumber: " + contractNumber + " not found.");
  }
}
