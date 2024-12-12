package com.qred.internalpaymentservice.payment.api;

import com.qred.internalpaymentservice.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Rest endpoint handler for creating payments, fetching payments, creating payments from file.
 *
 * @author georgemore on 2024-12-10
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments API", description = "Manage and process payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  @Operation(
      summary = "Create a new payment",
      description = "Creates a new payment record based on the provided PaymentDto.")
  public PaymentDto createPayment(@RequestBody @Valid PaymentDto paymentDto) {
    return paymentService.createPayment(paymentDto);
  }

  @PostMapping("/upload")
  @Operation(
      summary = "Process payments from a file",
      description = "Uploads a file and processes multiple payment records from the file.")
  public void processPaymentsFromFile(@RequestParam("file") MultipartFile file) {
    paymentService.processPaymentsFromFile(file);
  }

  @GetMapping
  @Operation(
      summary = "Get payments by contract number",
      description = "Retrieves a list of payments associated with a specific contract number.")
  public List<PaymentDto> getPayments(@RequestParam String contractNumber) {
    return paymentService.getPaymentsByContractNumber(contractNumber);
  }
}
