package co.uk.stefanpuia.backupr.config.reader;

import static org.assertj.core.api.BDDAssertions.then;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.cleanup.Cleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.ImmutableKeepAllCleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.ImmutableParameterizedCleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.KeepAllCleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.ParameterizedCleanup;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableLocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ImmutableLocalConfigSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConfigReaderCleanupTest extends AbstractConfigReaderTest {
  void shouldReadConfigWithCleanupDefinedInlineOnSource(
      final String cleanupJson, final Cleanup expected) {
    // Given
    final var cleanupInsert = cleanupJson != null ? ",\"cleanup\": %s".formatted(cleanupJson) : "";
    // language=JSON
    final var configStream =
        toInputStream(
            """
                {
                  "sources": [
                    {
                      "name": "local",
                      "type": "LOCAL",
                      "directory": "/tmp/backupr"
                      %s
                    }
                  ]
                }
                """
                .formatted(cleanupInsert));
    final var localSource =
        ImmutableLocalConfigSource.builder()
            .setName("local")
            .setEnabled(true)
            .setDirectory("/tmp/backupr")
            .setCleanup(expected)
            .build();

    // When
    final var config = configReader.readConfig(configStream);

    // Then
    then(config).isNotNull().isInstanceOf(BackuprConfig.class);
    then(config.getSources()).isNotNull().hasSize(1);
    then(config.getSources().get(0)).isEqualTo(localSource);
  }

  void shouldReadConfigWithCleanupDefinedInlineOnRemote(
      final String cleanupJson, final Cleanup expected) {
    // Given
    final var cleanupInsert = cleanupJson != null ? ",\"cleanup\": %s".formatted(cleanupJson) : "";
    // language=JSON
    final var configStream =
        toInputStream(
            """
                {
                  "remotes": [
                    {
                      "name": "local",
                      "type": "LOCAL",
                      "location": "/tmp/backupr"
                      %s
                    }
                  ]
                }
                """
                .formatted(cleanupInsert));
    final var localRemote =
        ImmutableLocalConfigRemote.builder()
            .setName("local")
            .setEnabled(true)
            .setLocation("/tmp/backupr")
            .setCleanup(expected)
            .setVariables(defaultVars)
            .build();

    // When
    final var config = configReader.readConfig(configStream);

    // Then
    then(config).isNotNull().isInstanceOf(BackuprConfig.class);
    then(config.getRemotes()).isNotNull().hasSize(1);
    then(config.getRemotes().get(0)).isEqualTo(localRemote);
  }

  void shouldReadConfigWithRemoteCleanupDefinedGlobally(
      final String cleanupJson, final String cleanupName, final Cleanup expected) {
    // Given
    final var cleanupInsert =
        cleanupJson != null ? ",\"cleanup\": [%s]".formatted(cleanupJson) : "";
    final var cleanupNameInsert =
        cleanupName != null ? ",\"cleanup\": \"%s\"".formatted(cleanupName) : "";
    // language=JSON
    final var configStream =
        toInputStream(
            """
                {
                  "remotes": [
                    {
                      "name": "local",
                      "type": "LOCAL",
                      "location": "/tmp/backupr"
                      %s
                    }
                  ]%s
                }
                """
                .formatted(cleanupNameInsert, cleanupInsert));
    final var localRemote =
        ImmutableLocalConfigRemote.builder()
            .setName("local")
            .setEnabled(true)
            .setLocation("/tmp/backupr")
            .setCleanup(expected)
            .setVariables(defaultVars)
            .build();

    // When
    final var config = configReader.readConfig(configStream);

    // Then
    then(config).isNotNull().isInstanceOf(BackuprConfig.class);
    then(config.getRemotes()).isNotNull().hasSize(1);
    then(config.getRemotes().get(0)).isEqualTo(localRemote);
  }

  void shouldReadConfigWithSourceCleanupDefinedGlobally(
      final String cleanupJson, final String cleanupName, final Cleanup expected) {
    // Given
    final var cleanupInsert =
        cleanupJson != null ? ",\"cleanup\": [%s]".formatted(cleanupJson) : "";
    final var cleanupNameInsert =
        cleanupName != null ? ",\"cleanup\": \"%s\"".formatted(cleanupName) : "";
    // language=JSON
    final var configStream =
        toInputStream(
            """
                {
                  "sources": [
                    {
                      "name": "local",
                      "type": "LOCAL",
                      "directory": "/tmp/backupr"
                      %s
                    }
                  ]%s
                }
                """
                .formatted(cleanupNameInsert, cleanupInsert));
    final var localSource =
        ImmutableLocalConfigSource.builder()
            .setName("local")
            .setEnabled(true)
            .setDirectory("/tmp/backupr")
            .setCleanup(expected)
            .build();

    // When
    final var config = configReader.readConfig(configStream);

    // Then
    then(config).isNotNull().isInstanceOf(BackuprConfig.class);
    then(config.getSources()).isNotNull().hasSize(1);
    then(config.getSources().get(0)).isEqualTo(localSource);
  }

  @Test
  void shouldReadConfigWithNoCleanupOnRemote() {
    shouldReadConfigWithCleanupDefinedInlineOnRemote(null, KeepAllCleanup.create());
  }

  @Test
  void shouldReadConfigWithNoCleanupOnSource() {
    shouldReadConfigWithCleanupDefinedInlineOnSource(null, null);
  }

  @Nested
  class KeepAll {
    @Test
    void shouldReadConfigCleanupKeepAllOnRemote() {
      shouldReadConfigWithCleanupDefinedInlineOnRemote(
          // language=JSON
          """
                {}
              """,
          ImmutableKeepAllCleanup.builder().setName("inline[local/KEEP_ALL]").build());
    }

    @Test
    void shouldReadConfigCleanupKeepAllOnSource() {
      shouldReadConfigWithCleanupDefinedInlineOnSource(
          // language=JSON
          """
                {}
              """,
          ImmutableKeepAllCleanup.builder().setName("inline[local/KEEP_ALL]").build());
    }
  }

  @Nested
  class Parameterized {
    @Nested
    class Inline {
      private final String cleanupJson =
          // language=JSON
          """
                {
                   "keepCount": 10,
                   "keepDays": 100
                }
              """;
      private final ParameterizedCleanup cleanupRule =
          ImmutableParameterizedCleanup.builder()
              .setName("inline[local/PARAMETERIZED]")
              .setKeepCount(10)
              .setKeepDays(100)
              .setEnabled(true)
              .build();

      @Test
      void shouldReadConfigParameterizedCleanupOnRemote() {
        shouldReadConfigWithCleanupDefinedInlineOnRemote(cleanupJson, cleanupRule);
      }

      @Test
      void shouldReadConfigParameterizedCleanupOnSource() {
        shouldReadConfigWithCleanupDefinedInlineOnSource(cleanupJson, cleanupRule);
      }
    }

    @Nested
    class Named {
      private final String cleanupJson =
          // language=JSON
          """
                {
                   "name": "cleanupRule",
                   "keepCount": 10,
                   "keepDays": 100
                }
              """;
      private final ParameterizedCleanup cleanupRule =
          ImmutableParameterizedCleanup.builder()
              .setName("cleanupRule")
              .setKeepCount(10)
              .setKeepDays(100)
              .setEnabled(true)
              .build();

      @Test
      void shouldReadConfigParameterizedCleanupOnRemoteWithNameRef() {
        shouldReadConfigWithRemoteCleanupDefinedGlobally(
            cleanupJson, cleanupRule.getName(), cleanupRule);
      }

      @Test
      void shouldReadConfigParameterizedCleanupOnSourceWithNameRef() {
        shouldReadConfigWithSourceCleanupDefinedGlobally(
            cleanupJson, cleanupRule.getName(), cleanupRule);
      }
    }
  }
}
