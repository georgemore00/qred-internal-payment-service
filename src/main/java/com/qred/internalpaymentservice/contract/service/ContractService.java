package com.qred.internalpaymentservice.contract.service;

import com.qred.internalpaymentservice.contract.entity.ContractEntity;
import com.qred.internalpaymentservice.contract.entity.ContractRepository;
import com.qred.internalpaymentservice.contract.exception.ContractNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service layer class for handling contract-related business logic.
 *
 * @author georgemore on 2024-12-10
 */
@Service
@RequiredArgsConstructor
public class ContractService {

  private final ContractRepository contractRepository;

  public ContractEntity findByContractNumberOrThrow(String contractNumber) {
    return contractRepository
        .findByContractNumber(contractNumber)
        .orElseThrow(() -> new ContractNotFoundException(contractNumber));
  }

  public Map<String, ContractEntity> findContractsByNumbers(List<String> contractNumbers) {
    List<ContractEntity> contractEntities =
        contractRepository.findByContractNumbersIn(contractNumbers);
    return contractEntities.stream()
        .collect(Collectors.toMap(ContractEntity::getContractNumber, Function.identity()));
  }
}
