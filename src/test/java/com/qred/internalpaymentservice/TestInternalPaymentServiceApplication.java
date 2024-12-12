package com.qred.internalpaymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.utility.TestcontainersConfiguration;

@SpringBootTest
public class TestInternalPaymentServiceApplication {

  public static void main(String[] args) {
    SpringApplication.from(InternalPaymentServiceApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
