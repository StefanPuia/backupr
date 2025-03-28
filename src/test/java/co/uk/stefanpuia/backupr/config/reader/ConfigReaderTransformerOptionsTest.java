package co.uk.stefanpuia.backupr.config.reader;

import static net.bytebuddy.utility.RandomString.make;
import static org.assertj.core.api.BDDAssertions.then;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ImmutableTarGzConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ImmutableZipConfigTransformerOptions;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConfigReaderTransformerOptionsTest extends AbstractConfigReaderTest {

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
