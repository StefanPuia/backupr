package co.uk.stefanpuia.backupr.config.reader;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import co.uk.stefanpuia.backupr.config.exception.ConfigFileReadException;
import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.cleanup.ImmutableParameterizedCleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.KeepAllCleanup;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableAzureClientSecretCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableBasicCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableNoneCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableAzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableGitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableLocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ImmutableLocalConfigSource;
import co.uk.stefanpuia.backupr.config.model.state.ImmutableBackupState;
import co.uk.stefanpuia.backupr.config.model.transformer.ImmutableZipConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.reader.mapper.VariablesWrapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

@SuppressWarnings("resource")
public class ConfigReaderTest extends AbstractConfigReaderTest {

  @Test
  void shouldReadConfig() {
    // Given
    // language=JSON
    final var configStream =
        toInputStream(
            """
            {
              "variables": {
                "abc": "123"
              },
              "state": {
                "remote": {
                  "type": "LOCAL",
                  "location": "/var/backups/state"
                }
              },
              "cleanup": [
                {
                  "name": "cleanupRule1",
                  "keepCount": 10,
                  "keepDays": 20
                }
              ],
              "credentials": [
                {
                  "name": "noneCred1"
                },
                {
                  "name": "basicCred1",
                  "username": "user1",
                  "password": "pass44"
                }
              ],
              "remotes": [
                {
                  "name": "localRemote1",
                  "type": "LOCAL",
                  "location": "/var/backups/foo",
                  "cleanup": "cleanupRule1"
                },
                {
                  "name": "gitRemote1",
                  "type": "GIT",
                  "url": "git:/var/backups/foo",
                  "branch": "master",
                  "credentials": "basicCred1",
                  "commitMessagePattern": "Some backup message <context.sourceName>"
                },
                {
                  "name": "azureRemote1",
                  "type": "AZURE_STORAGE_BLOB",
                  "endpoint": "endpoint",
                  "container": "container",
                  "overwrite": true
                }
              ],
              "sources": [
                {
                  "name": "localSource1",
                  "type": "LOCAL",
                  "directory": "/foo",
                  "files": [
                    "**/*.json"
                  ],
                  "transformers": [
                    "ZIP"
                  ],
                  "remotes": [
                    "localRemote1",
                    "azureRemote1"
                  ],
                  "cleanup": "cleanupRule1"
                },
                {
                  "name": "localSource2",
                  "type": "LOCAL",
                  "directory": "/foo",
                  "files": [
                    "**/*.json"
                  ],
                  "transformers": [
                    "ZIP"
                  ],
                  "remotes": [
                    {
                      "type": "LOCAL",
                      "location": "/var/backups/foo",
                      "cleanup": {
                        "keepDays": 3,
                        "disabled": true
                      }
                    },
                    "azureRemote1",
                    {
                      "type": "GIT",
                      "url": "git:/var/foo1",
                      "branch": "master",
                      "credentials": {
                        "username": "user2",
                        "password": "pas123"
                      }
                    },
                    {
                      "type": "GIT",
                      "url": "git:/var/foo2",
                      "branch": "master",
                      "credentials": "noneCred1"
                    }
                  ]
                }
              ]
            }
            """);
    // variables
    final var vars = new VariablesWrapper(Map.of("abc", "123"), System.getenv());

    // state
    final var state =
        ImmutableBackupState.of(
            ImmutableLocalConfigRemote.builder()
                .setLocation("/var/backups/state")
                .setEnabled(true)
                .setCleanup(KeepAllCleanup.create())
                .setVariables(vars)
                .build());

    // cleanup
    final var cleanupRule1 =
        ImmutableParameterizedCleanup.builder()
            .setName("cleanupRule1")
            .setKeepCount(10)
            .setKeepDays(20)
            .setEnabled(true)
            .build();

    // credentials
    final var noneCred1 = ImmutableNoneCredentials.builder().setName("noneCred1").build();
    final var basicCreds1 =
        ImmutableBasicCredentials.builder()
            .setName("basicCred1")
            .setUsername("user1")
            .setPassword("pass44")
            .build();

    // remotes
    final var localRemote1 =
        ImmutableLocalConfigRemote.builder()
            .setName("localRemote1")
            .setLocation("/var/backups/foo")
            .setEnabled(true)
            .setCleanup(cleanupRule1)
            .setVariables(vars)
            .build();
    final var gitRemote1 =
        ImmutableGitConfigRemote.builder()
            .setName("gitRemote1")
            .setEnabled(true)
            .setUrl("git:/var/backups/foo")
            .setBranch("master")
            .setCredentials(basicCreds1)
            .setCommitMessagePattern("Some backup message <context.sourceName>")
            .setVariables(vars)
            .build();
    final var azureRemote1 =
        ImmutableAzureStorageBlobConfigRemote.builder()
            .setName("azureRemote1")
            .setEnabled(true)
            .setEndpoint("endpoint")
            .setContainer("container")
            .setBlobPrefixPattern(
                "<context.sourceName>/<context.nowYear>/<context.nowMonth>/<context.nowDay>")
            .setVariables(vars)
            .setOverwrite(true)
            .setCleanup(KeepAllCleanup.create())
            .setCredentials(NoneCredentials.create())
            .build();

    // sources
    final var localSource1 =
        ImmutableLocalConfigSource.builder()
            .setName("localSource1")
            .setEnabled(true)
            .setDirectory("/foo")
            .setFiles(List.of("**/*.json"))
            .setCleanup(cleanupRule1)
            .setTransformers(
                List.of(
                    ImmutableZipConfigTransformerOptions.builder()
                        .setFilenamePattern("<context.sourceName>-<context.now>.zip")
                        .setVariables(vars)
                        .build()))
            .setRemotes(List.of(localRemote1, azureRemote1))
            .build();
    final var localSource2 =
        ImmutableLocalConfigSource.builder()
            .setName("localSource2")
            .setEnabled(true)
            .setDirectory("/foo")
            .setFiles(List.of("**/*.json"))
            .setTransformers(
                List.of(
                    ImmutableZipConfigTransformerOptions.builder()
                        .setFilenamePattern("<context.sourceName>-<context.now>.zip")
                        .setVariables(vars)
                        .build()))
            .setRemotes(
                List.of(
                    ImmutableLocalConfigRemote.builder()
                        .setName("inline[localSource2/LOCAL]")
                        .setLocation("/var/backups/foo")
                        .setEnabled(true)
                        .setVariables(vars)
                        .setCleanup(
                            ImmutableParameterizedCleanup.builder()
                                .setEnabled(false)
                                .setName("inline[inline[null/LOCAL]/PARAMETERIZED]")
                                .setKeepDays(3)
                                .build())
                        .build(),
                    azureRemote1,
                    ImmutableGitConfigRemote.builder()
                        .setEnabled(true)
                        .setName("inline[localSource2/GIT]")
                        .setUrl("git:/var/foo1")
                        .setBranch("master")
                        .setVariables(vars)
                        .setCommitMessagePattern("Automatic backup")
                        .setCredentials(
                            ImmutableBasicCredentials.builder()
                                .setName("inline[inline[localSource2/GIT]/BASIC]")
                                .setUsername("user2")
                                .setPassword("pas123")
                                .build())
                        .build(),
                    ImmutableGitConfigRemote.builder()
                        .setEnabled(true)
                        .setName("inline[localSource2/GIT]")
                        .setUrl("git:/var/foo2")
                        .setBranch("master")
                        .setCredentials(noneCred1)
                        .setCommitMessagePattern("Automatic backup")
                        .setVariables(vars)
                        .build()))
            .build();

    // When
    final var config = configReader.readConfig(configStream);

    // Then
    then(config).isNotNull().isInstanceOf(BackuprConfig.class);
    then(config.getRemotes())
        .isNotNull()
        .hasSize(3)
        .containsExactlyInAnyOrder(localRemote1, gitRemote1, azureRemote1);
    then(config.getSources())
        .isNotNull()
        .hasSize(2)
        .containsExactlyInAnyOrder(localSource1, localSource2);
    then(config.getState()).isEqualTo(state);
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
                    "defaultPassword": "<env.JUNIT_ENV_VALUE_2>",
                    "tenantId": "tenant-123"
                  },
                  "remotes": [
                    {
                      "name": "foo",
                      "type": "LOCAL",
                      "location": "<localBackupDestination>/<env.JUNIT_ENV_VALUE_1>/foo"
                    },
                    {
                      "name": "azureBlob",
                      "type": "AZURE_STORAGE_BLOB",
                      "endpoint": "endpoint",
                      "container": "container",
                      "credentials": {
                        "tenantId": "<tenantId>",
                        "clientId": "client-123",
                        "clientSecret": "<env.JUNIT_ENV_VALUE_2>"
                      }
                    },
                    {
                      "name": "git",
                      "type": "GIT",
                      "url": "<gitRemoteUrlRoot>/bar.git",
                      "branch": "<defaultGitBranch>",
                      "credentials": {
                        "username": "<defaultUsername>",
                        "password": "<defaultPassword>"
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
    final var vars =
        new VariablesWrapper(
            Map.of(
                "valueBeforeSomeProject", "/aaa//",
                "someProject", "abc123",
                "localBackupSource", "/etc/sources",
                "localBackupDestination", "/var/backups",
                "gitRemoteUrlRoot", "git://github.com/abc123",
                "defaultGitBranch", "main",
                "defaultUsername", "user",
                "defaultPassword", "fooBaz2566",
                "tenantId", "tenant-123"),
            System.getenv());
    final var localConfigRemote =
        ImmutableLocalConfigRemote.builder()
            .setName("foo")
            .setLocation("/var/backups/fooBar9998/foo")
            .setEnabled(true)
            .setVariables(vars)
            .setCleanup(KeepAllCleanup.create())
            .build();
    final var azureBlobConfigRemote =
        ImmutableAzureStorageBlobConfigRemote.builder()
            .setName("azureBlob")
            .setEnabled(true)
            .setEndpoint("endpoint")
            .setContainer("container")
            .setBlobPrefixPattern(
                "<context.sourceName>/<context.nowYear>/<context.nowMonth>/<context.nowDay>")
            .setVariables(vars)
            .setOverwrite(false)
            .setCleanup(KeepAllCleanup.create())
            .setCredentials(
                ImmutableAzureClientSecretCredentials.builder()
                    .setName("inline[azureBlob/AZURE_CLIENT_SECRET]")
                    .setTenantId("tenant-123")
                    .setClientId("client-123")
                    .setClientSecret("fooBaz2566")
                    .build())
            .build();
    final var gitConfigRemote =
        ImmutableGitConfigRemote.builder()
            .setName("git")
            .setEnabled(true)
            .setUrl("git://github.com/abc123/bar.git")
            .setBranch("main")
            .setVariables(vars)
            .setCommitMessagePattern("Automatic backup")
            .setCredentials(
                ImmutableBasicCredentials.builder()
                    .setName("inline[git/BASIC]")
                    .setUsername("user")
                    .setPassword("fooBaz2566")
                    .build())
            .build();

    // When
    final var config = configReader.readConfig(configStream);

    // Then
    then(config).isNotNull().isInstanceOf(BackuprConfig.class);
    then(config.getRemotes().get(0)).usingRecursiveComparison().isEqualTo(localConfigRemote);
    then(config.getRemotes().get(1)).usingRecursiveComparison().isEqualTo(azureBlobConfigRemote);
    then(config.getRemotes().get(2)).usingRecursiveComparison().isEqualTo(gitConfigRemote);
    then(config.getSources())
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
                            .setVariables(vars)
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
