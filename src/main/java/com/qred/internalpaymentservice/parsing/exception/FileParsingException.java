package com.qred.internalpaymentservice.parsing.exception;

/**
 * Thrown when an attempt to parse a file fails.
 *
 * @author georgemore on 2024-12-10
 */
public class FileParsingException extends RuntimeException {
  public FileParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public FileParsingException(String message) {
    super(message);
  }
}
