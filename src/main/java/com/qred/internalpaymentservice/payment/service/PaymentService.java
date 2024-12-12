package com.qred.internalpaymentservice.payment.service;

import com.qred.internalpaymentservice.contract.entity.ContractEntity;
import com.qred.internalpaymentservice.contract.exception.ContractNotFoundException;
import com.qred.internalpaymentservice.contract.service.ContractService;
import com.qred.internalpaymentservice.parsing.FileParserDelegate;
import com.qred.internalpaymentservice.payment.api.PaymentDto;
import com.qred.internalpaymentservice.payment.entity.PaymentEntity;
import com.qred.internalpaymentservice.payment.entity.PaymentRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service layer class for handling payment-related business logic. Handles creation of new payments
 * either manually or by file and fetching payments.
 *
 * @author georgemore on 2024-12-10
 */
@Service
@RequiredArgsConstructor
public class PaymentService {
  private final ModelMapper modelMapper;
  private final ContractService contractService;
  private final PaymentRepository paymentRepository;
  private final FileParserDelegate fileParserDelegate;
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  static final Logger LOG = LogManager.getLogger(PaymentService.class);

  @Transactional
  public PaymentDto createPayment(PaymentDto paymentDto) {
    ContractEntity contractEntity =
        contractService.findByContractNumberOrThrow(paymentDto.getContractNumber());
    PaymentEntity paymentEntity = modelMapper.map(paymentDto, PaymentEntity.class);

    paymentEntity.setContractEntity(contractEntity);
    PaymentEntity savedPaymentEntity = paymentRepository.save(paymentEntity);
    LOG.debug("Created payment with contract number: {}", paymentDto.getContractNumber());
    return modelMapper.map(savedPaymentEntity, PaymentDto.class);
  }

  @Transactional(readOnly = true)
  public List<PaymentDto> getPaymentsByContractNumber(String contractNumber) {
    List<PaymentEntity> paymentEntities = paymentRepository.findByContractNumber(contractNumber);
    return modelMapper.map(paymentEntities, new TypeToken<List<PaymentDto>>() {}.getType());
  }

  @Transactional
  public void processPaymentsFromFile(MultipartFile file) {
    List<PaymentDto> parsedPayments = fileParserDelegate.parsePaymentsFromFile(file);
    List<PaymentEntity> paymentsToBeCreated = this.processPayments(parsedPayments);
    paymentRepository.saveAll(paymentsToBeCreated);
    LOG.debug("Finished processing payment file: {}", file.getOriginalFilename());
  }

  /**
   * Processes a list of PaymentDto objects by validating constraints, fetching associated contracts
   * in a batch, and mapping them to PaymentEntity objects for batch saving.
   *
   * @param parsedPayments The list of PaymentDto objects to process.
   * @return A list of PaymentEntity objects with associated contracts, ready for persistence.
   */
  private List<PaymentEntity> processPayments(List<PaymentDto> parsedPayments) {
    this.validateDtoConstraints(parsedPayments);
    List<PaymentEntity> paymentsToBeCreated = new ArrayList<>();

    // Fetch all contracts in bulk by contract numbers
    List<String> contractNumbers =
        parsedPayments.stream().map(PaymentDto::getContractNumber).distinct().toList();

    Map<String, ContractEntity> contractMap =
        contractService.findContractsByNumbers(contractNumbers);

    for (PaymentDto parsedPayment : parsedPayments) {

      // For every payment validate the existence of the associated contract
      ContractEntity contractEntity = contractMap.get(parsedPayment.getContractNumber());
      if (contractEntity == null) {
        throw new ContractNotFoundException(parsedPayment.getContractNumber());
      }

      // Create PaymentEntity and set the contract association
      PaymentEntity paymentEntity = modelMapper.map(parsedPayment, PaymentEntity.class);
      paymentEntity.setContractEntity(contractEntity);
      paymentsToBeCreated.add(paymentEntity);
    }

    return paymentsToBeCreated;
  }

  private void validateDtoConstraints(List<PaymentDto> parsedPayments) {
    // Validate DTO constraints (e.g., positive amount)
    for (PaymentDto payment : parsedPayments) {
      Set<ConstraintViolation<PaymentDto>> violations = validator.validate(payment);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
    }
  }
}
