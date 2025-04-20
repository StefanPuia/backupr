package co.uk.stefanpuia.backupr.config.reader.dto;

import co.uk.stefanpuia.backupr.config.reader.dto.credentials.AzureCliCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.AzureClientSecretCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.BasicCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.CredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.ImmutableAzureCliCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.ImmutableAzureClientSecretCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.ImmutableBasicCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.ImmutableNoneCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.NoneCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.AzureStorageBlobConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.GitConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ImmutableAzureStorageBlobConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ImmutableGitConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ImmutableLocalConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.LocalConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.DockerCpConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ImmutableDockerCpConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ImmutableLocalConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.LocalConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.ImmutableTarGzTransformerOptionsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.ImmutableZipTransformerOptionsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.TarGzTransformerOptionsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.TransformerOptionsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.transformers.ZipTransformerOptionsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.validation.UniqueIdentifierValidator;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding({
  AzureCliCredentialsDto.class,
  AzureClientSecretCredentialsDto.class,
  AzureStorageBlobConfigRemoteDto.class,
  BackuprConfigDto.class,
  BasicCredentialsDto.class,
  ConfigRemoteDto.class,
  ConfigSourceDto.class,
  CredentialsDto.class,
  GitConfigRemoteDto.class,
  ImmutableAzureCliCredentialsDto.class,
  ImmutableAzureClientSecretCredentialsDto.class,
  ImmutableAzureStorageBlobConfigRemoteDto.class,
  ImmutableBackuprConfigDto.class,
  ImmutableBasicCredentialsDto.class,
  ImmutableGitConfigRemoteDto.class,
  ImmutableLocalConfigRemoteDto.class,
  ImmutableLocalConfigSourceDto.class,
  ImmutableDockerCpConfigSourceDto.class,
  ImmutableNoneCredentialsDto.class,
  ImmutableTarGzTransformerOptionsDto.class,
  ImmutableZipTransformerOptionsDto.class,
  LocalConfigRemoteDto.class,
  LocalConfigSourceDto.class,
  DockerCpConfigSourceDto.class,
  NoneCredentialsDto.class,
  TarGzTransformerOptionsDto.class,
  TransformerOptionsDto.class,
  ZipTransformerOptionsDto.class,
  UniqueIdentifierValidator.class,
})
public class DtoRuntimeHints {}
