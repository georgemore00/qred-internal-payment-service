package com.qred.internalpaymentservice.tests.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qred.internalpaymentservice.parsing.FileParserDelegate;
import com.qred.internalpaymentservice.parsing.csv.CsvFileParser;
import com.qred.internalpaymentservice.parsing.exception.PaymentFileEmptyException;
import com.qred.internalpaymentservice.parsing.exception.PaymentFileTypeNotSupportedException;
import com.qred.internalpaymentservice.parsing.xml.XmlFileParser;
import com.qred.internalpaymentservice.payment.api.PaymentDto;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileParserDelegateTest {

  @Mock private CsvFileParser csvParser;
  @Mock private XmlFileParser xmlParser;

  private FileParserDelegate fileParserDelegate;
  private MockMultipartFile mockCsvFile;
  private MockMultipartFile mockXmlFile;
  private MockMultipartFile mockPdfFile;

  @BeforeEach
  void setUp() {
    // Setup mock file types with appropriate file types
    mockCsvFile =
        new MockMultipartFile("file", "test.csv", "text/csv", "some CSV content".getBytes());
    mockXmlFile =
        new MockMultipartFile("file", "test.xml", "application/xml", "some XML content".getBytes());
    mockPdfFile =
        new MockMultipartFile("file", "test.pdf", "application/pdf", "some PDF content".getBytes());

    // Mock the behavior of the parsers
    when(csvParser.getSupportedFileType()).thenReturn("csv");
    when(xmlParser.getSupportedFileType()).thenReturn("xml");

    // Manually inject the parsers into the service
    fileParserDelegate = new FileParserDelegate(List.of(csvParser, xmlParser));
  }

  @Test
  void testParserFileTypeMapInitialization() {
    assertTrue(fileParserDelegate.getParserFileTypeMap().containsKey("csv"));
    assertTrue(fileParserDelegate.getParserFileTypeMap().containsKey("xml"));
  }

  @Test
  void testParsePaymentsFromFile_shouldCallCsvParser_whenFileTypeCsv() {
    // Given
    when(csvParser.parseFile(mockCsvFile)).thenReturn(List.of(new PaymentDto()));

    // When
    List<PaymentDto> result = fileParserDelegate.parsePaymentsFromFile(mockCsvFile);

    // Then
    verify(csvParser).parseFile(mockCsvFile);
    assertEquals(1, result.size());
  }

  @Test
  void testParsePaymentsFromFile_shouldCallXmlParser_whenFileTypeXml() {
    // Given
    when(xmlParser.parseFile(mockXmlFile)).thenReturn(List.of(new PaymentDto()));

    // When
    List<PaymentDto> result = fileParserDelegate.parsePaymentsFromFile(mockXmlFile);

    // Then
    verify(xmlParser).parseFile(mockXmlFile);
    assertEquals(1, result.size());
  }

  @Test
  void
      testParsePaymentsFromFile_shouldThrowPaymentFileEmptyException_whenPaymentFileWithoutPaymentsIsProvided() {
    when(csvParser.parseFile(mockCsvFile)).thenReturn(new ArrayList<>());
    when(xmlParser.parseFile(mockXmlFile)).thenReturn(new ArrayList<>());

    assertThrows(
        PaymentFileEmptyException.class,
        () -> fileParserDelegate.parsePaymentsFromFile(mockCsvFile));

    assertThrows(
        PaymentFileEmptyException.class,
        () -> fileParserDelegate.parsePaymentsFromFile(mockXmlFile));
  }

  @Test
  void testParsePaymentsFromFile_shouldThrowPaymentFileTypeNotSupportedException_whenFileTypePdf() {
    assertThrows(
        PaymentFileTypeNotSupportedException.class,
        () -> fileParserDelegate.parsePaymentsFromFile(mockPdfFile));
  }
}
