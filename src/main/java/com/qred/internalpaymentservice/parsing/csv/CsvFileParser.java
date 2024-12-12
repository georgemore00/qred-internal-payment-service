package com.qred.internalpaymentservice.parsing.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.qred.internalpaymentservice.parsing.FileParser;
import com.qred.internalpaymentservice.parsing.exception.FileParsingException;
import com.qred.internalpaymentservice.payment.api.PaymentDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of the FileParser interface responsible for parsing CSV files. Uses the OpenCSV
 * library to parse the files. Uses ModelMapper to map the parsed data to a List of PaymentDtos.
 *
 * @author georgemore on 2024-12-10
 */
@Component
@RequiredArgsConstructor
public class CsvFileParser implements FileParser {
  private final ModelMapper modelMapper;
  static final Logger LOG = LogManager.getLogger(CsvFileParser.class);

  /**
   * @param file file to be parsed
   * @return list of parsed paymentDtos
   * @throws FileParsingException for null/empty files, I/O, OpenCSV parsing exceptions
   */
  @Override
  public List<PaymentDto> parseFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new FileParsingException("Attempted to parse empty or null file.");
    }

    try (InputStream inputStream = file.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

      CsvToBean<CsvPaymentDto> csvToBean =
          new CsvToBeanBuilder<CsvPaymentDto>(bufferedReader)
              .withType(CsvPaymentDto.class)
              .withIgnoreLeadingWhiteSpace(true)
              .build();

      List<CsvPaymentDto> parsedCsvPayments = csvToBean.parse();
      return modelMapper.map(parsedCsvPayments, new TypeToken<List<PaymentDto>>() {}.getType());

    } catch (IOException e) {
      String errorMessage =
          String.format("Failed to parse the CSV file: %s due to I/O.", file.getOriginalFilename());
      LOG.error(errorMessage, e);
      throw new FileParsingException(errorMessage, e);
    } catch (RuntimeException e) {
      String errorMessage =
          String.format(
              "Unexpected error while processing the CSV file: %s", file.getOriginalFilename());
      LOG.error(errorMessage, e);
      throw new FileParsingException(errorMessage, e);
    }
  }

  @Override
  public String getSupportedFileType() {
    return "csv";
  }
}
