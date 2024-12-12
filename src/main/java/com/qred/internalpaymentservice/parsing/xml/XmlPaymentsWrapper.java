package com.qred.internalpaymentservice.parsing.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * Class that acts as a wrapper/container to hold payments for XML parsing purposes.
 *
 * @author georgemore on 2024-12-10
 */
@JacksonXmlRootElement(localName = "payments")
@Data
public class XmlPaymentsWrapper {

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "payment")
  private List<XmlPaymentDto> payments;
}
