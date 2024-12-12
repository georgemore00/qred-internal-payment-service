package com.qred.internalpaymentservice.parsing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a payment file's type is not supported.
 *
 * @author georgemore on 2024-12-10
 */
@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class PaymentFileTypeNotSupportedException extends RuntimeException {
  public PaymentFileTypeNotSupportedException(String fileType) {
    super("Payment file of type: " + fileType + " is not supported.");
  }
}
