package com.qred.internalpaymentservice.tests.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qred.internalpaymentservice.contract.entity.ContractEntity;
import com.qred.internalpaymentservice.contract.service.ContractService;
import com.qred.internalpaymentservice.parsing.FileParserDelegate;
import com.qred.internalpaymentservice.payment.api.PaymentDto;
import com.qred.internalpaymentservice.payment.entity.PaymentEntity;
import com.qred.internalpaymentservice.payment.entity.PaymentRepository;
import com.qred.internalpaymentservice.payment.entity.PaymentType;
import com.qred.internalpaymentservice.contract.exception.ContractNotFoundException;
import com.qred.internalpaymentservice.payment.service.PaymentService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author georgemore on 2024-12-11
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Spy private ModelMapper modelMapper;
  @Spy private Validator validator;
  @Mock private ContractService contractService;
  @Mock private PaymentRepository paymentRepository;
  @Mock private FileParserDelegate fileParserDelegate;
  @InjectMocks private PaymentService paymentService;

  @Test
  void testCreatePayment() {
    // Given
    ContractEntity contractEntity = new ContractEntity(2L, null, "12345", new ArrayList<>());
    PaymentEntity savedPaymentEntity =
        new PaymentEntity(
            1L, LocalDate.now(), new BigDecimal("1000.00"), PaymentType.INCOMING, contractEntity);
    savedPaymentEntity.setContractEntity(contractEntity);

    PaymentDto paymentDto =
        new PaymentDto(
            null, LocalDate.now(), new BigDecimal("1000.00"), PaymentType.INCOMING, "12345");

    // Mock method calls to dependencies
    when(contractService.findByContractNumberOrThrow(paymentDto.getContractNumber()))
        .thenReturn(contractEntity);
    when(paymentRepository.save(any())).thenReturn(savedPaymentEntity);

    // When
    PaymentDto createdPaymentDto = paymentService.createPayment(paymentDto);

    // Then
    assertNotNull(createdPaymentDto);
    assertEquals(paymentDto.getContractNumber(), createdPaymentDto.getContractNumber());
    assertEquals(paymentDto.getAmount(), createdPaymentDto.getAmount());

    // Verify mocks were called
    verify(contractService).findByContractNumberOrThrow(paymentDto.getContractNumber());
    verify(paymentRepository).save(any());
  }

  @Test
  void testGetPaymentsByContractNumber() {
    // Given
    String contractNumber = "12345";
    ContractEntity contractEntity = new ContractEntity(2L, null, contractNumber, new ArrayList<>());
    List<PaymentEntity> paymentEntities =
        List.of(
            new PaymentEntity(
                null,
                LocalDate.now(),
                new BigDecimal("1000.00"),
                PaymentType.INCOMING,
                contractEntity));

    when(paymentRepository.findByContractNumber(contractNumber)).thenReturn(paymentEntities);

    // When
    List<PaymentDto> fetchedPaymentDtos =
        paymentService.getPaymentsByContractNumber(contractNumber);

    // Then
    assertEquals(paymentEntities.size(), fetchedPaymentDtos.size());
    assertEquals(
        paymentEntities.get(0).getContractEntity().getContractNumber(),
        fetchedPaymentDtos.get(0).getContractNumber());

    verify(paymentRepository).findByContractNumber(contractNumber);
  }

  @Test
  void testProcessPaymentsFromFile_shouldProcessValidPayments() {
    // Given
    MultipartFile file = mock(MultipartFile.class); // Mock file
    PaymentDto validPaymentDto =
        new PaymentDto(
            null, LocalDate.now(), new BigDecimal("1000.00"), PaymentType.INCOMING, "12345");
    ContractEntity contractEntity = new ContractEntity(2L, null, "12345", new ArrayList<>());
    List<PaymentDto> paymentDtos = List.of(validPaymentDto);

    // Mock file parsing
    when(fileParserDelegate.parsePaymentsFromFile(file)).thenReturn(paymentDtos);
    when(contractService.findContractsByNumbers(List.of("12345")))
        .thenReturn(Map.of("12345", contractEntity));

    // When
    paymentService.processPaymentsFromFile(file);

    // Then
    // Verify the interaction with the file parser and contract service
    verify(fileParserDelegate).parsePaymentsFromFile(file);
    verify(contractService).findContractsByNumbers(List.of("12345"));
    verify(paymentRepository).saveAll(anyList());
  }

  @Test
  void testProcessPaymentsFromFile_shouldThrowConstraintViolationExceptionForInvalidAmount() {
    // Given
    MultipartFile file = mock(MultipartFile.class); // Mock file
    PaymentDto invalidPaymentDto =
        new PaymentDto(
            null, LocalDate.now(), new BigDecimal("-1000.00"), PaymentType.INCOMING, "12345");
    List<PaymentDto> paymentDtos = List.of(invalidPaymentDto);

    // Mock file parsing
    when(fileParserDelegate.parsePaymentsFromFile(file)).thenReturn(paymentDtos);

    // When & Then
    assertThrows(
        ConstraintViolationException.class, () -> paymentService.processPaymentsFromFile(file));

    // Verify the file parsing but do not proceed to contract fetching or saving
    verify(fileParserDelegate).parsePaymentsFromFile(file);
    verify(contractService, never()).findContractsByNumbers(anyList());
    verify(paymentRepository, never()).saveAll(anyList());
  }

  @Test
  void testProcessPaymentsFromFile_shouldThrowContractNotFoundExceptionForInvalidContractNumber() {
    // Given
    MultipartFile file = mock(MultipartFile.class); // Mock file
    PaymentDto paymentDtoWithInvalidContract =
        new PaymentDto(
            null,
            LocalDate.now(),
            new BigDecimal("1000.00"),
            PaymentType.INCOMING,
            "99999"); // Invalid contract number
    List<PaymentDto> paymentDtos = List.of(paymentDtoWithInvalidContract);

    // Mock file parsing
    when(fileParserDelegate.parsePaymentsFromFile(file)).thenReturn(paymentDtos);
    when(contractService.findContractsByNumbers(List.of("99999")))
        .thenReturn(Map.of()); // No contract found for "99999"

    // When & Then
    assertThrows(
        ContractNotFoundException.class, () -> paymentService.processPaymentsFromFile(file));

    // Verify the file parsing and contract service interaction
    verify(fileParserDelegate).parsePaymentsFromFile(file);
    verify(contractService).findContractsByNumbers(List.of("99999"));
    verify(paymentRepository, never()).saveAll(anyList());
  }
}
