package co.uk.stefanpuia.backupr.config.reader;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import org.junit.jupiter.api.Test;

public class ConfigReaderValidateTest extends AbstractConfigReaderTest {

  @Test
  void shouldFailValidationWhenIdentifierDoesNotMatchPattern() {
    // Given
    // language=JSON
    final var configStream =
        toInputStream(
            """
        {
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

  @Test
  void shouldFailValidationWhenCredentialNotExists() {
    // Given
    // language=JSON
    final var configStream =
        toInputStream(
            """
            {
              "credentials": [
                {
                  "name": "none"
                }
              ],
              "remotes": [
                {
                  "name": "git",
                  "type": "GIT",
                  "url": "/home/foo",
                  "branch": "main",
                  "credentials": "basicAuth"
                }
              ]
            }
            """);

    // When - Then
    thenThrownBy(() -> configReader.readConfig(configStream))
        .isInstanceOf(ConfigValidationException.class)
        .hasMessageContainingAll("in remote 'git'", "no credentials named 'basicAuth' defined");
  }

  @Test
  void shouldFailValidationWhenDuplicateIdentifiers() {
    // Given
    // language=JSON
    final var configStream =
        toInputStream(
            """
            {
              "sources": [
                {
                  "name": "ff",
                  "type": "LOCAL",
                  "directory": "/foo",
                  "files": [
                    "**/*.json"
                  ]
                },
                {
                  "name": "ff",
                  "type": "LOCAL",
                  "directory": "/foo",
                  "files": [
                    "**/*.json"
                  ]
                }
              ],
              "remotes": [
                {
                  "name": "git",
                  "type": "GIT",
                  "url": "/home/foo",
                  "branch": "main"
                },
                {
                  "name": "git",
                  "type": "GIT",
                  "url": "/home/foo",
                  "branch": "main"
                }
              ]
            }
            """);

    // When - Then
    thenThrownBy(() -> configReader.readConfig(configStream))
        .isInstanceOf(ConfigValidationException.class)
        .hasMessageContainingAll(
            "sources: configuration names must be unique",
            "remotes: configuration names must be unique");
  }
}
