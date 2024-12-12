package com.qred.internalpaymentservice.parsing.xml;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qred.internalpaymentservice.parsing.FileParser;
import com.qred.internalpaymentservice.parsing.exception.FileParsingException;
import com.qred.internalpaymentservice.payment.api.PaymentDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of the FileParser interface responsible for parsing XML files. Uses the JacksonXML
 * library to parse the files. Uses ModelMapper to map the parsed data to a List of PaymentDtos.
 *
 * @author georgemore on 2024-12-10
 */
@Component
@RequiredArgsConstructor
public class XmlFileParser implements FileParser {
  static final Logger LOG = LogManager.getLogger(XmlFileParser.class);

  private final ModelMapper modelMapper;
  private final ObjectMapper xmlMapper =
      new XmlMapper()
          .registerModule(new JavaTimeModule())
          .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

  /**
   * Parses the given XML file and returns a list of PaymentDto objects.
   *
   * @param file the file to be parsed
   * @return list of parsed PaymentDto objects
   * @throws FileParsingException for null/empty files, I/O errors, or XML parsing exceptions
   */
  @Override
  public List<PaymentDto> parseFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new FileParsingException("Attempted to parse empty or null file.");
    }

    try (InputStream inputStream = file.getInputStream()) {
      XmlPaymentsWrapper parsedXmlPayments =
          xmlMapper.readValue(inputStream, XmlPaymentsWrapper.class);
      return modelMapper.map(
          parsedXmlPayments.getPayments(), new TypeToken<List<PaymentDto>>() {}.getType());

    } catch (IOException e) {
      String errorMessage =
          String.format(
              "Failed to parse the XML file: %s due to I/O error.", file.getOriginalFilename());
      LOG.error(errorMessage, e);
      throw new FileParsingException(errorMessage, e);

    } catch (RuntimeException e) {
      String errorMessage =
          String.format(
              "Unexpected error while processing the XML file: %s", file.getOriginalFilename());
      LOG.error(errorMessage, e);
      throw new FileParsingException(errorMessage, e);
    }
  }

  @Override
  public String getSupportedFileType() {
    return "xml";
  }
}
