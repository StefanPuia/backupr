package co.uk.stefanpuia.backupr.config.reader;

import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigCleanupMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigCredentialsMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigDtoMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigRemoteMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigSourceMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigStateMapper;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigTransformerMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.CoreDtoMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.VariablesWrapper;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.BackupSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    classes = {
      ConfigReader.class,
      ObjectMapper.class,
      ValidationAutoConfiguration.class,
      ConfigDtoMapperImpl.class,
      ConfigRemoteMapperImpl.class,
      ConfigSourceMapperImpl.class,
      CoreDtoMapperImpl.class,
      ConfigCredentialsMapperImpl.class,
      ConfigCleanupMapperImpl.class,
      ConfigTransformerMapperImpl.class,
      ConfigStateMapper.class,
      StringTemplateRenderer.class
    })
@DisabledInAotMode
public abstract class AbstractConfigReaderTest {
  protected final VariablesWrapper defaultVars = new VariablesWrapper(Map.of(), System.getenv());
  @Autowired protected ConfigReader configReader;

  @MockitoBean protected BackupSession backupSession;
  @MockitoBean protected BackupHelper backupHelper;

  protected InputStream toInputStream(final String input) {
    return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
  }
}
