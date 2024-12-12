package com.qred.internalpaymentservice.parsing.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.qred.internalpaymentservice.payment.entity.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for serializing payment data from XML files. Uses JacksonXML
 * annotations for parsing purposes.
 *
 * @author georgemore on 2024-12-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "payment")
public class XmlPaymentDto {

  @JacksonXmlProperty(localName = "payment_date")
  private LocalDate paymentDate;

  @JacksonXmlProperty(localName = "amount")
  private BigDecimal amount;

  @JacksonXmlProperty(localName = "type")
  private PaymentType paymentType;

  @JacksonXmlProperty(localName = "contract_number")
  private String contractNumber;
}
