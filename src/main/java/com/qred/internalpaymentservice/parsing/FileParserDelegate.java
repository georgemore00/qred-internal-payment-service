package com.qred.internalpaymentservice.parsing;

import com.qred.internalpaymentservice.parsing.exception.PaymentFileEmptyException;
import com.qred.internalpaymentservice.parsing.exception.PaymentFileTypeNotSupportedException;
import com.qred.internalpaymentservice.payment.api.PaymentDto;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class that delegates the parsing of Multipart files to implementations of FileParser (Strategy pattern)
 * allowing for flexible handling of different file formats.
 *
 * @author georgemore on 2024-12-10
 */
@Component
@Getter
public class FileParserDelegate {
  static final Logger LOG = LogManager.getLogger(FileParserDelegate.class);
  private final Map<String, FileParser> parserFileTypeMap = new HashMap<>();

  public FileParserDelegate(List<FileParser> parsers) {
    parsers.forEach(p -> parserFileTypeMap.put(p.getSupportedFileType(), p));
  }

  public List<PaymentDto> parsePaymentsFromFile(MultipartFile file) {
    String fileType = StringUtils.getFilenameExtension(file.getOriginalFilename());
    FileParser parser = this.getFileParserByFileType(fileType);

    LOG.debug("Started parsing of file: {}", file.getOriginalFilename());

    List<PaymentDto> parsedPaymentDtos = parser.parseFile(file);
    if (parsedPaymentDtos.isEmpty()) {
      throw new PaymentFileEmptyException(file.getOriginalFilename());
    }

    return parsedPaymentDtos;
  }

  private FileParser getFileParserByFileType(String fileType) {
    FileParser parser = parserFileTypeMap.get(fileType);

    if (parser == null) {
      throw new PaymentFileTypeNotSupportedException(fileType);
    }
    return parser;
  }
}
