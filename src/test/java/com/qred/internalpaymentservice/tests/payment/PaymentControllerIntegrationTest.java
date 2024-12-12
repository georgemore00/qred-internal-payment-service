package com.qred.internalpaymentservice.tests.payment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qred.internalpaymentservice.client.ClientDetails;
import com.qred.internalpaymentservice.client.ClientEntity;
import com.qred.internalpaymentservice.client.ClientRepository;
import com.qred.internalpaymentservice.contract.entity.ContractEntity;
import com.qred.internalpaymentservice.contract.entity.ContractRepository;
import com.qred.internalpaymentservice.payment.api.PaymentDto;
import com.qred.internalpaymentservice.payment.entity.PaymentType;
import com.qred.internalpaymentservice.utils.PaymentTestHelper;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author georgemore on 2024-12-11
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class PaymentControllerIntegrationTest {

  public static final String PAYMENT_API_BASE_ENDPOINT = "/api/v1/payments";
  public static final String PAYMENT_API_UPLOAD_FILE_ENDPOINT = "/api/v1/payments/upload";

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:15.2")
          .withDatabaseName("test")
          .withUsername("user")
          .withPassword("password");

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

  @Autowired private ClientRepository clientRepository;

  @Autowired private ContractRepository contractRepository;

  private ContractEntity firstContractEntity;
  private ContractEntity secondContractEntity;

  @PostConstruct
  void setupInitialData() {
    // save client
    ClientDetails clientDetails = new ClientDetails("John", "Doe");
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setClientDetails(clientDetails);
    clientEntity.setId(1L);
    ClientEntity savedClientEntity = clientRepository.save(clientEntity);

    // save 2 contracts with contract number 12345 and 54321
    firstContractEntity = new ContractEntity(2L, savedClientEntity, "12345", new ArrayList<>());
    firstContractEntity = contractRepository.save(firstContractEntity);

    secondContractEntity = new ContractEntity(3L, savedClientEntity, "54321", new ArrayList<>());
    secondContractEntity = contractRepository.save(secondContractEntity);
  }

  @Test
  void testCreateAndGetPayments() throws Exception {
    // Assert no existing payments for contract before
    mockMvc
        .perform(
            get(PAYMENT_API_BASE_ENDPOINT)
                .param("contractNumber", firstContractEntity.getContractNumber()))
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));

    // Create payment
    BigDecimal amount = new BigDecimal("1000.00");
    PaymentDto paymentDto =
        new PaymentDto(
            null,
            LocalDate.now(),
            amount,
            PaymentType.INCOMING,
            firstContractEntity.getContractNumber());
    mockMvc
        .perform(
            post(PAYMENT_API_BASE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contractNumber").value(firstContractEntity.getContractNumber()))
        .andExpect(
            jsonPath("$.amount").value(amount.setScale(1))); // Jackson strips trailing zeroes

    // Assert there is a payment for contract number after creating
    mockMvc
        .perform(
            get(PAYMENT_API_BASE_ENDPOINT)
                .param("contractNumber", firstContractEntity.getContractNumber()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].contractNumber").value(firstContractEntity.getContractNumber()))
        .andExpect(
            jsonPath("$[0].amount").value(amount.setScale(1))); // Jackson strips trailing zeroes
  }

  @Test
  void testCreatePayment_withNegativeAmount_shouldReturnErrorMessage() throws Exception {
    PaymentDto invalidPaymentDto =
        new PaymentDto(
            null,
            LocalDate.now(),
            new BigDecimal("-10.00"),
            PaymentType.INCOMING,
            firstContractEntity.getContractNumber());

    mockMvc
        .perform(
            post(PAYMENT_API_BASE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPaymentDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.amount").value("Amount must be greater than 0."));
  }

  @Test
  void testProcessPaymentsFromCsv() throws Exception {
    // Simulate a valid CSV file
    MockMultipartFile csvFile =
        PaymentTestHelper.getMockFileFromResources("valid_payments.csv", "csv");

    // upload payment file
    mockMvc
        .perform(multipart(PAYMENT_API_UPLOAD_FILE_ENDPOINT).file(csvFile))
        .andExpect(status().isOk());

    // Verify 2 payments were made for both contract numbers
    mockMvc
        .perform(
            get(PAYMENT_API_BASE_ENDPOINT)
                .param("contractNumber", firstContractEntity.getContractNumber()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));

    mockMvc
        .perform(
            get(PAYMENT_API_BASE_ENDPOINT)
                .param("contractNumber", secondContractEntity.getContractNumber()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void testProcessPaymentsFromXml() throws Exception {
    // Simulate a valid XML file
    MockMultipartFile xmlFile =
        PaymentTestHelper.getMockFileFromResources("valid_payments.xml", "xml");

    // upload payment file
    mockMvc
        .perform(multipart(PAYMENT_API_UPLOAD_FILE_ENDPOINT).file(xmlFile))
        .andExpect(status().isOk());

    // Verify 2 payments were made for both contract numbers
    mockMvc
        .perform(
            get(PAYMENT_API_BASE_ENDPOINT)
                .param("contractNumber", firstContractEntity.getContractNumber()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));

    mockMvc
        .perform(
            get(PAYMENT_API_BASE_ENDPOINT)
                .param("contractNumber", secondContractEntity.getContractNumber()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void testProcessPaymentsUnsupportedFileType() throws Exception {
    // Simulate an unsupported PDF file
    MockMultipartFile pdfFile =
        PaymentTestHelper.getMockFileFromResources("some_payments.pdf", "pdf");

    mockMvc
        .perform(multipart(PAYMENT_API_UPLOAD_FILE_ENDPOINT).file(pdfFile))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  void testProcessPaymentsEmptyFile() throws Exception {
    // Simulate an empty CSV file
    MockMultipartFile emptyCsvFile = PaymentTestHelper.getMockFileFromResources("empty.csv", "csv");

    mockMvc
        .perform(multipart(PAYMENT_API_UPLOAD_FILE_ENDPOINT).file(emptyCsvFile))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Attempted to parse empty or null file."));
  }
}
