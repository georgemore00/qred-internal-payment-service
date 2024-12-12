package com.qred.internalpaymentservice.parsing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown whenever a Payment file not contains payment records.
 *
 * @author georgemore on 2024-12-11
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PaymentFileEmptyException extends RuntimeException {

  public PaymentFileEmptyException(String originalFilename) {
    super("Provided file: " + originalFilename + " contained no payment records");
  }
}
