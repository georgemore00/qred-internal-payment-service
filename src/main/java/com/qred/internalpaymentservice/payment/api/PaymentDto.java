package com.qred.internalpaymentservice.payment.api;

import com.qred.internalpaymentservice.payment.entity.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for transferring payment data. Uses Jakarta validation API to validate
 * input values.
 *
 * @author georgemore on 2024-12-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

  private Long id;

  @NotNull(message = "Payment date is required.")
  @PastOrPresent(message = "Payment date cannot be in the future.")
  private LocalDate paymentDate;

  @NotNull(message = "Amount is required.")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
  private BigDecimal amount;

  @NotNull(message = "Payment type is required.")
  private PaymentType paymentType;

  @NotBlank(message = "Contract number is required.")
  private String contractNumber;
}
