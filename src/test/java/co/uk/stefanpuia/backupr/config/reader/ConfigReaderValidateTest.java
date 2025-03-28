package co.uk.stefanpuia.backupr.config.reader;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import org.junit.jupiter.api.Test;

public class ConfigReaderValidateTest extends AbstractConfigReaderTest {

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
                    "git"
                  ]
                }
              ]
            }
            """);

    // When - Then
    thenThrownBy(() -> configReader.readConfig(configStream))
        .isInstanceOf(ConfigValidationException.class)
        .hasMessageContainingAll("in remote 'git'", "no credentials named 'basicAuth' defined");
  }
}
