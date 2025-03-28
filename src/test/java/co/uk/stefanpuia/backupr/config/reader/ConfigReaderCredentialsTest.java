package co.uk.stefanpuia.backupr.config.reader;

import static org.assertj.core.api.BDDAssertions.then;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableGitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.ImmutableBasicCredentials;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.ImmutableNoneCredentials;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConfigReaderCredentialsTest extends AbstractConfigReaderTest {

  void shouldReadConfigWithCredentialsDefinedDirectly(
      final String credentialsJson, final Credentials expected) {
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

  void shouldReadConfigWithCredentialsDefinedSeparately(
      final String credentialsJson, final String credentialsName, final Credentials expected) {
    // Given
    final var credentialsInsert =
        credentialsJson != null ? ",\"credentials\": [%s]".formatted(credentialsJson) : "";
    final var credentialsNameInsert =
        credentialsName != null ? ",\"credentials\": \"%s\"".formatted(credentialsName) : "";
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
                  ]%s
                }
                """
                .formatted(credentialsNameInsert, credentialsInsert));
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
    // then(config.credentials()).isNotNull().hasSize(1).containsExactlyInAnyOrder(expected);
    then(config.remotes()).isNotNull().hasSize(1).containsExactlyInAnyOrder(gitConfigRemote);
  }

  @Test
  void shouldReadConfigWithNoCredentials() {
    shouldReadConfigWithCredentialsDefinedDirectly(null, null);
  }

  @Nested
  class None {
    @Test
    void shouldReadConfigCredentialsNone() {
      shouldReadConfigWithCredentialsDefinedDirectly(
          // language=JSON
          """
                {}
              """, ImmutableNoneCredentials.builder().build());
    }

    @Test
    void shouldReadConfigCredentialsNoneWithNameRef() {
      shouldReadConfigWithCredentialsDefinedSeparately(
          // language=JSON
          """
                {
                   "name": "noneCreds"
                }
              """,
          "noneCreds",
          ImmutableNoneCredentials.builder().setName("noneCreds").build());
    }
  }

  @Nested
  class Basic {
    @Test
    void shouldReadConfigCredentialsBasicJustUsername() {
      shouldReadConfigWithCredentialsDefinedDirectly(
          // language=JSON
          """
                {
                   "username": "foo"
                }
              """,
          ImmutableBasicCredentials.builder().setUsername("foo").build());
    }

    @Test
    void shouldReadConfigCredentialsBasicJustUsernameWithNameRef() {
      shouldReadConfigWithCredentialsDefinedSeparately(
          // language=JSON
          """
                {
                   "name": "basicCreds",
                   "username": "foo"
                }
              """,
          "basicCreds",
          ImmutableBasicCredentials.builder().setName("basicCreds").setUsername("foo").build());
    }

    @Test
    void shouldReadConfigCredentialsBasic() {
      shouldReadConfigWithCredentialsDefinedDirectly(
          // language=JSON
          """
                {
                   "username": "foo",
                   "password": "bar"
                }
              """,
          ImmutableBasicCredentials.builder().setUsername("foo").setPassword("bar").build());
    }

    @Test
    void shouldReadConfigCredentialsBasicWithNameRef() {
      shouldReadConfigWithCredentialsDefinedSeparately(
          // language=JSON
          """
                {
                   "name": "basicCreds1",
                   "username": "foo",
                   "password": "bar"
                }
              """,
          "basicCreds1",
          ImmutableBasicCredentials.builder()
              .setName("basicCreds1")
              .setUsername("foo")
              .setPassword("bar")
              .build());
    }
  }
}
