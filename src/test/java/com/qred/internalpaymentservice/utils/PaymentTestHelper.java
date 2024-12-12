package com.qred.internalpaymentservice.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.qred.internalpaymentservice.payment.api.PaymentDto;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author georgemore on 2024-12-10
 */
public final class PaymentTestHelper {

  private PaymentTestHelper() {
    // Prevent instantiation
  }

  /** Asserts that the provided Dtos have matching values */
  public static void assertPaymentDto(PaymentDto expected, PaymentDto actual) {

    assertEquals(
        expected.getPaymentDate(), actual.getPaymentDate(), "Payment date does not match.");
    assertEquals(expected.getAmount(), actual.getAmount(), "Amount does not match.");
    assertEquals(
        expected.getPaymentType(), actual.getPaymentType(), "Payment type does not match.");
    assertEquals(
        expected.getContractNumber(),
        actual.getContractNumber(),
        "Contract number does not match.");
  }

  /**
   * Loads a file from the `src/test/resources` directory for testing purposes.
   *
   * @param fileName the name of the file in the test resources folder
   * @param fileType the type of the file (used for folder structure)
   * @return a MultipartFile instance containing the file
   * @throws IOException if the file cannot be found or read
   */
  public static MultipartFile getFileFromResources(String fileName, String fileType)
      throws IOException {
    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("File name must not be null or empty");
    }
    if (fileType == null || fileType.isEmpty()) {
      throw new IllegalArgumentException("File type must not be null or empty");
    }

    String resourcePath = Paths.get("testData", fileType, fileName).toString();

    InputStream inputStream =
        PaymentTestHelper.class.getClassLoader().getResourceAsStream(resourcePath);

    assertNotNull(inputStream, "Test file not found at path: " + resourcePath);
    return new MockMultipartFile(fileName, inputStream);
  }

  /**
   * Loads a file from the `src/test/resources` directory for testing purposes.
   *
   * @param fileName the name of the file in the test resources folder
   * @param fileType the type of the file (used for folder structure)
   * @return a MultipartFile instance containing the file
   * @throws IOException if the file cannot be found or read
   */
  public static MockMultipartFile getMockFileFromResources(String fileName, String fileType)
      throws IOException {
    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("File name must not be null or empty");
    }
    if (fileType == null || fileType.isEmpty()) {
      throw new IllegalArgumentException("File type must not be null or empty");
    }

    String resourcePath = Paths.get("testData", fileType, fileName).toString();

    InputStream inputStream =
        PaymentTestHelper.class.getClassLoader().getResourceAsStream(resourcePath);

    assertNotNull(inputStream, "Test file not found at path: " + resourcePath);
    MockMultipartFile file =
        new MockMultipartFile("file", fileName, "some content type", inputStream);
    return file;
  }
}
