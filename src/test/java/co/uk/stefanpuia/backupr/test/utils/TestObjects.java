package co.uk.stefanpuia.backupr.test.utils;

import co.uk.stefanpuia.backupr.config.model.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableAzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ImmutableLocalConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import co.uk.stefanpuia.backupr.config.reader.mapper.VariablesWrapper;
import java.util.Map;

public class TestObjects {
  public static AzureStorageBlobConfigRemote azureStorageBlobConfigRemote() {
    return ImmutableAzureStorageBlobConfigRemote.builder()
        .setName("azureStorageBlobConfigRemote")
        .setEnabled(true)
        .setEndpoint("endpoint")
        .setContainer("container")
        .setBlobPrefixPattern("2025")
        .setCredentials(NoneCredentials.create())
        .setVariables(new VariablesWrapper(Map.of(), Map.of()))
        .setOverwrite(true)
        .build();
  }

  public static LocalConfigSource configSource() {
    return ImmutableLocalConfigSource.builder()
        .setName("configSource")
        .setDirectory("/base/config/source/dir")
        .setEnabled(true)
        .build();
  }
}
