package co.uk.stefanpuia.backupr.config;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import co.uk.stefanpuia.backupr.config.exception.ConfigFileReadException;
import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.SourceTransformer;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableAzureStorageConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableLocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ImmutableLocalConfigSource;
import co.uk.stefanpuia.backupr.config.reader.ConfigReader;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigCredentialsMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigDtoMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigRemoteMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigSourceMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.CoreDtoMapperImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.aot.DisabledInAotMode;

@SuppressWarnings("resource")
@SpringBootTest(
    classes = {
      ConfigReader.class,
      ObjectMapper.class,
      ValidationAutoConfiguration.class,
      ConfigDtoMapperImpl.class,
      ConfigRemoteMapperImpl.class,
      ConfigSourceMapperImpl.class,
      CoreDtoMapperImpl.class,
      ConfigCredentialsMapperImpl.class
    })
@DisabledInAotMode
public class ConfigReaderTest {
  @Autowired private ConfigReader configReader;

  private InputStream toInputStream(final String input) {
    return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
  }

  @Nested
  class ReadConfig {
    @Test
    void shouldReadConfig() {
      // Given
      final var configStream =
          toInputStream(
              """
              {
                "remotes": [
                  {
                    "name": "foo",
                    "type": "LOCAL",
                    "location": "/home/foo"
                  },
                  {
                    "name": "azure",
                    "type": "AZURE_STORAGE"
                  }
                ],
                "sources": [
                  {
                    "name": "ff",
                    "directory": "/foo",
                    "files": [
                      "**/*.json"
                    ],
                    "transformers": [
                      "ZIP"
                    ],
                    "remotes": [
                      "foo",
                      "azure"
                    ]
                  }
                ]
              }
              """);
      final var localConfigRemote =
          ImmutableLocalConfigRemote.builder()
              .setName("foo")
              .setLocation("/home/foo")
              .setEnabled(true)
              .build();
      final var azureConfigRemote =
          ImmutableAzureStorageConfigRemote.builder().setName("azure").setEnabled(true).build();

      // When
      final var config = configReader.readConfig(configStream);

      // Then
      then(config).isNotNull().isInstanceOf(BackuprConfig.class);
      then(config.remotes())
          .isNotNull()
          .hasSize(2)
          .containsExactlyInAnyOrder(localConfigRemote, azureConfigRemote);

      then(config.sources())
          .isNotNull()
          .hasSize(1)
          .containsExactlyInAnyOrder(
              ImmutableLocalConfigSource.builder()
                  .setName("ff")
                  .setEnabled(true)
                  .setDirectory("/foo")
                  .setFiles(List.of("**/*.json"))
                  .setTransformers(List.of(SourceTransformer.ZIP))
                  .setRemotes(List.of(localConfigRemote, azureConfigRemote))
                  .build());
    }

    @Test
    void shouldFailReadingConfigWhenUnrecognizedFields() {
      // Given
      final var configStream =
          toInputStream(
              """
              {
                "aaa": 1,
                "remotes": [
                  {
                    "name": "foo",
                    "type": "LOCAL",
                    "location": "/home/foo"
                  }
                ],
                "sources": [
                  {
                    "name": "ff",
                    "directory": "/foo",
                    "files": [
                      "**/*.json"
                    ],
                    "transformers": [
                      "ZIP"
                    ],
                    "remotes": [
                      "azure"
                    ]
                  }
                ]
              }
              """);

      // When - Then
      thenThrownBy(() -> configReader.readConfig(configStream))
          .isInstanceOf(ConfigFileReadException.class)
          .hasMessageContainingAll("Unrecognized field \"aaa\"");
    }
  }

  @Nested
  class Validate {
    @Test
    void shouldFailValidationWhenRemotesEmpty() {
      // Given
      final var configStream =
          toInputStream(
              """
          {
            "remotes": [],
            "sources": [
              {
                "name": "ff",
                "directory": "/foo",
                "files": [
                  "**/*.json"
                ],
                "transformers": [
                  "ZIP"
                ],
                "remotes": [
                  "azure"
                ]
              }
            ]
          }
          """);

      // When - Then
      thenThrownBy(() -> configReader.readConfig(configStream))
          .isInstanceOf(ConfigValidationException.class)
          .hasMessageContainingAll("remotes: must not be empty");
    }

    @Test
    void shouldFailValidationWhenSourcesEmpty() {
      // Given
      final var configStream =
          toInputStream(
              """
          {
            "remotes": [
              {
                "name": "foo",
                "type": "LOCAL",
                "location": "/home/foo"
              }
            ],
            "sources": []
          }
          """);

      // When - Then
      thenThrownBy(() -> configReader.readConfig(configStream))
          .isInstanceOf(ConfigValidationException.class)
          .hasMessageContainingAll("sources: must not be empty");
    }

    @Test
    void shouldFailValidationWhenRemoteNotExists() {
      // Given
      final var configStream =
          toInputStream(
              """
              {
                "remotes": [
                  {
                    "name": "foo",
                    "type": "LOCAL",
                    "location": "/home/foo"
                  }
                ],
                "sources": [
                  {
                    "name": "ff",
                    "directory": "/foo",
                    "files": [
                      "**/*.json"
                    ],
                    "transformers": [
                      "ZIP"
                    ],
                    "remotes": [
                      "azure"
                    ]
                  }
                ]
              }
              """);

      // When - Then
      thenThrownBy(() -> configReader.readConfig(configStream))
          .isInstanceOf(ConfigValidationException.class)
          .hasMessageContainingAll("in source 'ff'", "no remote named 'azure' defined");
    }
  }
}
