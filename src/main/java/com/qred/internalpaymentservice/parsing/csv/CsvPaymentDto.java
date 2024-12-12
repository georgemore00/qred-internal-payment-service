package com.qred.internalpaymentservice.parsing.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.qred.internalpaymentservice.payment.entity.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for serializing payment data from CSV files.
 *
 * @author georgemore on 2024-12-10
 */
@Data
public class CsvPaymentDto {

  @CsvBindByName(column = "payment_date")
  @CsvDate(value = "yyyy-MM-dd")
  private LocalDate paymentDate;

  @CsvBindByName(column = "amount")
  private BigDecimal amount;

  @CsvBindByName(column = "type")
  private PaymentType paymentType;

  @CsvBindByName(column = "contract_number")
  private String contractNumber;
}
