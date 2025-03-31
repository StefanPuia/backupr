package co.uk.stefanpuia.backupr.config.reader;

import static java.util.Objects.requireNonNull;

import co.uk.stefanpuia.backupr.config.exception.ConfigFileReadException;
import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.reader.dto.BackuprConfigDto;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.util.BindErrorUtils;

@Slf4j
@Service
@AllArgsConstructor
public class ConfigReader {

  private final ObjectMapper objectMapper;
  private final Validator validator;
  private final ConfigDtoMapper mapper;

  public BackuprConfig readConfig(final InputStream inputStream) {
    try {
      final var configDto = objectMapper.readValue(inputStream, BackuprConfigDto.class);
      validate(configDto, "configuration file");
      final var config = requireNonNull(mapper.convert(configDto));
      validate(config, "configuration");
      return config;
    } catch (IOException e) {
      throw new ConfigFileReadException(e);
    }
  }

  private void validate(final Object config, final String objectName) {
    final var errors = new BeanPropertyBindingResult(config, objectName);
    validator.validate(config, errors);

    if (errors.getErrorCount() > 0) {
      throw new ConfigValidationException(BindErrorUtils.resolveAndJoin(errors.getAllErrors()));
    }
  }
}
