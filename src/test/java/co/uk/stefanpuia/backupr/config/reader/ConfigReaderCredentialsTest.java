package co.uk.stefanpuia.backupr.config.reader;

import static org.assertj.core.api.BDDAssertions.then;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableBasicCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableNoneCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableGitConfigRemote;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConfigReaderCredentialsTest extends AbstractConfigReaderTest {

  void shouldReadConfigWithCredentialsDefinedInline(
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
            .setCommitMessagePattern("Automatic backup")
            .setCredentials(expected)
            .setVariables(defaultVars)
            .build();

    // When
    final var config = configReader.readConfig(configStream);

    // Then
    then(config).isNotNull().isInstanceOf(BackuprConfig.class);
    then(config.remotes()).isNotNull().hasSize(1).containsExactlyInAnyOrder(gitConfigRemote);
  }

  void shouldReadConfigWithCredentialsDefinedGlobally(
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
            .setCredentials(expected)
            .setCommitMessagePattern("Automatic backup")
            .setVariables(defaultVars)
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
    shouldReadConfigWithCredentialsDefinedInline(null, NoneCredentials.create());
  }

  @Nested
  class None {
    @Test
    void shouldReadConfigCredentialsNone() {
      shouldReadConfigWithCredentialsDefinedInline(
          // language=JSON
          """
                {}
              """,
          ImmutableNoneCredentials.builder().setName("inline[git/NONE]").build());
    }

    @Test
    void shouldReadConfigCredentialsNoneWithNameRef() {
      shouldReadConfigWithCredentialsDefinedGlobally(
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
      shouldReadConfigWithCredentialsDefinedInline(
          // language=JSON
          """
                {
                   "username": "foo"
                }
              """,
          ImmutableBasicCredentials.builder()
              .setName("inline[git/BASIC]")
              .setUsername("foo")
              .build());
    }

    @Test
    void shouldReadConfigCredentialsBasicJustUsernameWithNameRef() {
      shouldReadConfigWithCredentialsDefinedGlobally(
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
      shouldReadConfigWithCredentialsDefinedInline(
          // language=JSON
          """
                {
                   "username": "foo",
                   "password": "bar"
                }
              """,
          ImmutableBasicCredentials.builder()
              .setName("inline[git/BASIC]")
              .setUsername("foo")
              .setPassword("bar")
              .build());
    }

    @Test
    void shouldReadConfigCredentialsBasicWithNameRef() {
      shouldReadConfigWithCredentialsDefinedGlobally(
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
