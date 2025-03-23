package co.uk.stefanpuia.backupr.config;

import static net.bytebuddy.utility.RandomString.make;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import co.uk.stefanpuia.backupr.config.exception.ConfigFileReadException;
import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableAzureStorageConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableGitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableLocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.BasicCredentials;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.config.model.source.ImmutableLocalConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ImmutableTarGzConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ImmutableZipConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.reader.ConfigReader;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigCredentialsMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigDtoMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigRemoteMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigSourceMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.ConfigTransformerMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.CoreDtoMapperImpl;
import co.uk.stefanpuia.backupr.config.reader.mapper.VariablesWrapper;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
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
      ConfigCredentialsMapperImpl.class,
      ConfigTransformerMapperImpl.class,
      StringTemplateRenderer.class
    })
@DisabledInAotMode
public class ConfigReaderTest {
  final VariablesWrapper defaultVars = new VariablesWrapper(Map.of(), System.getenv());
  @Autowired private ConfigReader configReader;

  private InputStream toInputStream(final String input) {
    return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
  }

  @Nested
  class ReadConfig {
    @Test
    void shouldReadConfig() {
      // Given
      // language=JSON
      final var configStream =
          toInputStream(
              """
              {
                "remotes": [
                  {
                    "name": "foo",
                    "type": "LOCAL",
                    "location": "/var/backups/foo"
                  },
                  {
                    "name": "azure",
                    "type": "AZURE_STORAGE"
                  }
                ],
                "sources": [
                  {
                    "name": "ff",
                    "type": "LOCAL",
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
              .setLocation("/var/backups/foo")
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
                  .setTransformers(
                      List.of(
                          ImmutableZipConfigTransformerOptions.builder()
                              .setFilenamePattern("<context.sourceName>-<context.now>.zip")
                              .setVariables(defaultVars)
                              .build()))
                  .setRemotes(List.of(localConfigRemote, azureConfigRemote))
                  .build());
    }

    @Test
    @SetEnvironmentVariable(key = "JUNIT_ENV_VALUE_1", value = "fooBar9998")
    @SetEnvironmentVariable(key = "JUNIT_ENV_VALUE_2", value = "fooBaz2566")
    void shouldReadConfigWithTemplateVariables() {
      // Given
      // language=JSON
      final var configStream =
          toInputStream(
              """
                  {
                    "variables": {
                      "valueBeforeSomeProject": "/aaa/<someProject>/",
                      "someProject": "abc123",
                      "localBackupSource": "/etc/sources",
                      "localBackupDestination": "/var/backups",
                      "gitRemoteUrlRoot": "git://github.com/<someProject>",
                      "defaultGitBranch": "main",
                      "defaultUsername": "user",
                      "defaultPassword": "<env.JUNIT_ENV_VALUE_2>"
                    },
                    "remotes": [
                      {
                        "name": "foo",
                        "type": "LOCAL",
                        "location": "<localBackupDestination>/<env.JUNIT_ENV_VALUE_1>/foo"
                      },
                      {
                        "name": "azure",
                        "type": "AZURE_STORAGE"
                      },
                      {
                        "name": "git",
                        "type": "GIT",
                        "url": "<gitRemoteUrlRoot>/bar.git",
                        "branch": "<defaultGitBranch>",
                        "credentials": {
                          "basic": {
                            "username": "<defaultUsername>",
                            "password": "<defaultPassword>"
                          }
                        }
                      }
                    ],
                    "sources": [
                      {
                        "name": "ff",
                        "type": "LOCAL",
                        "directory": "<localBackupSource>/foo",
                        "files": [
                          "<valueBeforeSomeProject>",
                          "**/<someProject>/*.json",
                          "**/*.json"
                        ],
                        "transformers": [
                          "ZIP"
                        ],
                        "remotes": [
                          "foo",
                          "git"
                        ]
                      }
                    ]
                  }
                  """);
      final var localConfigRemote =
          ImmutableLocalConfigRemote.builder()
              .setName("foo")
              .setLocation("/var/backups/fooBar9998/foo")
              .setEnabled(true)
              .build();
      final var azureConfigRemote =
          ImmutableAzureStorageConfigRemote.builder().setName("azure").setEnabled(true).build();
      final var gitConfigRemote =
          ImmutableGitConfigRemote.builder()
              .setName("git")
              .setEnabled(true)
              .setUrl("git://github.com/abc123/bar.git")
              .setBranch("main")
              .setCredentials(new BasicCredentials("user", "fooBaz2566"))
              .build();

      // When
      final var config = configReader.readConfig(configStream);

      // Then
      then(config).isNotNull().isInstanceOf(BackuprConfig.class);
      then(config.remotes())
          .isNotNull()
          .hasSize(3)
          .containsExactlyInAnyOrder(localConfigRemote, azureConfigRemote, gitConfigRemote);
      then(config.sources())
          .isNotNull()
          .hasSize(1)
          .containsExactlyInAnyOrder(
              ImmutableLocalConfigSource.builder()
                  .setName("ff")
                  .setEnabled(true)
                  .setDirectory("/etc/sources/foo")
                  .setFiles(List.of("/aaa//", "**/abc123/*.json", "**/*.json"))
                  .setTransformers(
                      List.of(
                          ImmutableZipConfigTransformerOptions.builder()
                              .setFilenamePattern("<context.sourceName>-<context.now>.zip")
                              .setVariables(
                                  new VariablesWrapper(
                                      Map.of(
                                          "valueBeforeSomeProject", "/aaa//",
                                          "someProject", "abc123",
                                          "localBackupSource", "/etc/sources",
                                          "localBackupDestination", "/var/backups",
                                          "gitRemoteUrlRoot", "git://github.com/abc123",
                                          "defaultGitBranch", "main",
                                          "defaultUsername", "user",
                                          "defaultPassword", "fooBaz2566"),
                                      System.getenv()))
                              .build()))
                  .setRemotes(List.of(localConfigRemote, gitConfigRemote))
                  .build());
    }

    @Test
    void shouldFailReadingConfigWhenUnrecognizedFields() {
      // Given
      // language=JSON
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
                    "type": "LOCAL",
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
      // language=JSON
      final var configStream =
          toInputStream(
              """
          {
            "remotes": [],
            "sources": [
              {
                "name": "ff",
                "type": "LOCAL",
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
      // language=JSON
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
    void shouldFailValidationWhenIdentifierWrong() {
      // Given
      // language=JSON
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
                "name": "$$$",
                "type": "LOCAL",
                "directory": "/foo"
              }
            ]
          }
          """);

      // When - Then
      thenThrownBy(() -> configReader.readConfig(configStream))
          .isInstanceOf(ConfigValidationException.class)
          .hasMessageContainingAll("sources[0].name: must match \"^[0-9a-zA-Z\\-_.]+$");
    }

    @Test
    void shouldFailValidationWhenRemoteNotExists() {
      // Given
      // language=JSON
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
                    "type": "LOCAL",
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

  @Nested
  class ReadConfigCredentials {
    void shouldReadConfigWithCredentials(final String credentialsJson, final Credentials expected) {
      // Given
      final var credentialsInsert =
          credentialsJson != null ? ",\"credentials\": %s".formatted(credentialsJson) : "";
      // language=JSON
      final var configStream =
          toInputStream(
              """
                  {
                    "remotes": [
                      {
                        "name": "git",
                        "type": "GIT",
                        "url": "git://github.com/abc123/bar.git",
                        "branch": "main"
                        %s
                      }
                    ],
                    "sources": [
                      {
                        "name": "local",
                        "type": "LOCAL",
                        "directory": "/foo",
                        "remotes": [
                          "git"
                        ]
                      }
                    ]
                  }
                  """
                  .formatted(credentialsInsert));
      final var gitConfigRemote =
          ImmutableGitConfigRemote.builder()
              .setName("git")
              .setEnabled(true)
              .setUrl("git://github.com/abc123/bar.git")
              .setBranch("main")
              .setCredentials(Optional.ofNullable(expected))
              .build();

      // When
      final var config = configReader.readConfig(configStream);

      // Then
      then(config).isNotNull().isInstanceOf(BackuprConfig.class);
      then(config.remotes()).isNotNull().hasSize(1).containsExactlyInAnyOrder(gitConfigRemote);
    }

    @Test
    void shouldReadConfigWithNoCredentials() {
      shouldReadConfigWithCredentials(null, null);
    }

    @Test
    void shouldReadConfigCredentialsNone() {
      shouldReadConfigWithCredentials(
          // language=JSON
          """
                {
                   "none": {}
                }
              """,
          new NoneCredentials());
    }

    @Test
    void shouldReadConfigCredentialsBasicJustUsername() {
      shouldReadConfigWithCredentials(
          // language=JSON
          """
                {
                   "basic": {
                      "username": "foo"
                   }
                }
              """,
          new BasicCredentials("foo", null));
    }

    @Test
    void shouldReadConfigCredentialsBasic() {
      shouldReadConfigWithCredentials(
          // language=JSON
          """
                {
                   "basic": {
                      "username": "foo",
                      "password": "bar"
                   }
                }
              """,
          new BasicCredentials("foo", "bar"));
    }
  }

  @Nested
  class ReadConfigTransformerOptions {
    final ConfigTransformerOptions defaultZip =
        ImmutableZipConfigTransformerOptions.builder()
            .setFilenamePattern("<context.sourceName>-<context.now>.zip")
            .setVariables(defaultVars)
            .build();

    void shouldReadConfigWithTransformers(
        final String transformersJsonArray, final List<ConfigTransformerOptions> expected) {
      // Given
      final var transformersInsert =
          transformersJsonArray != null
              ? ",\"transformers\": %s".formatted(transformersJsonArray)
              : "";
      // language=JSON
      final var configStream =
          toInputStream(
              """
                  {
                    "remotes": [
                      {
                        "name": "local",
                        "type": "LOCAL",
                        "location": "/local"
                      }
                    ],
                    "sources": [
                      {
                        "name": "local",
                        "type": "LOCAL",
                        "directory": "/foo",
                        "remotes": [
                          "local"
                        ]
                        %s
                      }
                    ]
                  }
                  """
                  .formatted(transformersInsert));

      // When
      final var config = configReader.readConfig(configStream);

      // Then
      then(config).isNotNull().isInstanceOf(BackuprConfig.class);
      then(config.sources()).isNotNull().hasSize(1);
      final var actual = config.sources().get(0).getTransformers()
          // .stream()
          // .map(
          //     t -> {
          //       final var variables =
          //           new VariablesWrapper(
          //               t.getVariables().variables(), Map.of(), t.getVariables().context());
          //       return switch (t) {
          //         case ZipConfigTransformerOptions zip ->
          //             ImmutableZipConfigTransformerOptions.builder()
          //                 .from(zip)
          //                 .setVariables(variables)
          //                 .build();
          //         case TarGzConfigTransformerOptions targz ->
          //             ImmutableTarGzConfigTransformerOptions.builder()
          //                 .from(targz)
          //                 .setVariables(variables)
          //                 .build();
          //         default -> throw new IllegalStateException("Unexpected value: " + t);
          //       };
          //     })
          // .toList()
          ;
      then(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void shouldReadConfigWithNoTransformers() {
      shouldReadConfigWithTransformers(null, List.of());
    }

    @Test
    void shouldReadConfigWithMultipleTransformerEnum() {
      shouldReadConfigWithTransformers(
          // language=JSON
          """
                ["ZIP", "ZIP"]
              """, List.of(defaultZip, defaultZip));
    }

    @Test
    void shouldReadConfigWithMultipleTransformerDefault() {
      shouldReadConfigWithTransformers(
          // language=JSON
          """
                [{"zip": {}}, {"zip": {}}]
              """,
          List.of(defaultZip, defaultZip));
    }

    @Test
    void shouldReadConfigWithMultipleCombined() {
      shouldReadConfigWithTransformers(
          // language=JSON
          """
                ["ZIP", {"zip": {}}, {"zip": {}}, "ZIP"]
              """,
          List.of(defaultZip, defaultZip, defaultZip, defaultZip));
    }

    @Nested
    class Zip {
      @Test
      void shouldReadConfigWithZipTransformerEnum() {
        shouldReadConfigWithTransformers(
            // language=JSON
            """
                  ["ZIP"]
                """, List.of(defaultZip));
      }

      @Test
      void shouldReadConfigWithZipTransformerDefault() {
        shouldReadConfigWithTransformers(
            // language=JSON
            """
                  [{"zip": {}}]
                """, List.of(defaultZip));
      }

      @Test
      void shouldReadConfigWithZipTransformerAllOptions() {
        final String pattern = make();
        shouldReadConfigWithTransformers(
            // language=JSON
            """
                  [{"zip": { "filenamePattern": "%s" }}]
                """
                .formatted(pattern),
            List.of(
                ImmutableZipConfigTransformerOptions.builder()
                    .setFilenamePattern(pattern)
                    .setVariables(defaultVars)
                    .build()));
      }
    }

    @Nested
    class TarGz {
      @Test
      void shouldReadConfigWithTarGzTransformerEnum() {
        shouldReadConfigWithTransformers(
            // language=JSON
            """
                  ["TARGZ"]
                """,
            List.of(
                ImmutableTarGzConfigTransformerOptions.builder()
                    .setFilenamePattern("<context.sourceName>-<context.now>.tar.gz")
                    .setVariables(defaultVars)
                    .build()));
      }

      @Test
      void shouldReadConfigWithTarGzTransformerDefault() {
        shouldReadConfigWithTransformers(
            // language=JSON
            """
                  [{"targz": {}}]
                """,
            List.of(
                ImmutableTarGzConfigTransformerOptions.builder()
                    .setFilenamePattern("<context.sourceName>-<context.now>.tar.gz")
                    .setVariables(defaultVars)
                    .build()));
      }

      @Test
      void shouldReadConfigWithTarGzTransformerAllOptions() {
        final String pattern = make();
        shouldReadConfigWithTransformers(
            // language=JSON
            """
                  [{"targz": { "filenamePattern": "%s" }}]
                """
                .formatted(pattern),
            List.of(
                ImmutableTarGzConfigTransformerOptions.builder()
                    .setFilenamePattern(pattern)
                    .setVariables(defaultVars)
                    .build()));
      }
    }
  }
}
