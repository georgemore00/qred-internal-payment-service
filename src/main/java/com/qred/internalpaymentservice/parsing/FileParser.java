package com.qred.internalpaymentservice.parsing;

import com.qred.internalpaymentservice.payment.api.PaymentDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * This interface defines the contract for any file parser class responsible for reading and
 * processing payment files (such as CSV, XML, etc.) and transforming the data into a list of
 * PaymentDto objects.
 *
 * @author georgemore on 2024-12-10
 */
public interface FileParser {
  List<PaymentDto> parseFile(MultipartFile file);

  String getSupportedFileType();
}
