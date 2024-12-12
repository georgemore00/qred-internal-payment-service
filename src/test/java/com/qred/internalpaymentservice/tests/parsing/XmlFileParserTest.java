package com.qred.internalpaymentservice.tests.parsing;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.qred.internalpaymentservice.parsing.exception.FileParsingException;
import com.qred.internalpaymentservice.parsing.xml.XmlFileParser;
import com.qred.internalpaymentservice.payment.api.PaymentDto;
import com.qred.internalpaymentservice.payment.entity.PaymentType;
import com.qred.internalpaymentservice.utils.PaymentTestHelper;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author georgemore on 2024-12-10
 */
@ExtendWith(MockitoExtension.class)
class XmlFileParserTest {

  @Spy private ModelMapper modelMapper;

  @InjectMocks private XmlFileParser xmlFileParser;

  @Test
  void testParseFile_shouldReturnPaymentDtos_whenValidXmlFileProvided() throws IOException {
    // Given
    MultipartFile file = PaymentTestHelper.getFileFromResources("valid_payments.xml", "xml");
    PaymentDto expectedDtoFirst =
        new PaymentDto(
            0L,
            LocalDate.of(2024, 1, 30),
            new BigDecimal("1000.00"),
            PaymentType.INCOMING,
            "12345");

    PaymentDto expectedDtoSecond =
        new PaymentDto(
            0L, LocalDate.of(2024, 1, 31), new BigDecimal("500.00"), PaymentType.OUTGOING, "54321");
    // When
    List<PaymentDto> paymentDtos = xmlFileParser.parseFile(file);

    // Then
    PaymentTestHelper.assertPaymentDto(expectedDtoFirst, paymentDtos.get(0));
    PaymentTestHelper.assertPaymentDto(expectedDtoSecond, paymentDtos.get(1));
  }

  @ParameterizedTest
  @MethodSource("getNamesOfInvalidFilesForExceptionTests")
  void testParseFile_shouldThrowFileParsingException_whenInvalidFile(String fileName)
      throws IOException {
    MultipartFile file = PaymentTestHelper.getFileFromResources(fileName, "xml");

    assertThrows(FileParsingException.class, () -> xmlFileParser.parseFile(file));
  }

  static List<String> getNamesOfInvalidFilesForExceptionTests() {
    return List.of(
        "payments_missing_root_element.xml",
        "payments_unclosed_fields.xml",
        "payments_invalid_field_value.xml",
        "payments_invalid_payment_date_format.xml");
  }
}
