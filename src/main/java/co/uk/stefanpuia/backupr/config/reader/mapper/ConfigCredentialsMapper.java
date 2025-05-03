package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.credentials.AzureCliCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.AzureClientSecretCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.BasicCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableAzureCliCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableAzureClientSecretCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableBasicCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.ImmutableNoneCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.AzureCliCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.AzureClientSecretCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.BasicCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.CredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.NoneCredentialsDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import jakarta.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    uses = CoreDtoMapper.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigCredentialsMapper {

  @SubclassMapping(target = NoneCredentials.class, source = NoneCredentialsDto.class)
  @SubclassMapping(target = BasicCredentials.class, source = BasicCredentialsDto.class)
  @SubclassMapping(target = AzureCliCredentials.class, source = AzureCliCredentialsDto.class)
  @SubclassMapping(
      target = AzureClientSecretCredentials.class,
      source = AzureClientSecretCredentialsDto.class)
  protected abstract Credentials mapCredential(
      CredentialsDto source, @Context String remoteName, @Context VariablesWrapper variables);

  protected abstract NoneCredentials convert(
      NoneCredentialsDto source, @Context String remoteName, @Context VariablesWrapper variables);

  @Mapping(target = "username", source = "username", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "password", source = "password", qualifiedByName = "applyTemplateToString")
  protected abstract BasicCredentials convert(
      BasicCredentialsDto source, @Context String remoteName, @Context VariablesWrapper variables);

  @Mapping(target = "tenantId", source = "tenantId", qualifiedByName = "applyTemplateToString")
  protected abstract AzureCliCredentials convert(
      AzureCliCredentialsDto source,
      @Context String remoteName,
      @Context VariablesWrapper variables);

  @Mapping(target = "tenantId", source = "tenantId", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "clientId", source = "clientId", qualifiedByName = "applyTemplateToString")
  @Mapping(
      target = "clientSecret",
      source = "clientSecret",
      qualifiedByName = "applyTemplateToString")
  protected abstract AzureClientSecretCredentials convert(
      AzureClientSecretCredentialsDto source,
      @Context String remoteName,
      @Context VariablesWrapper variables);

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableNoneCredentials.Builder target,
      final @Context String remoteName) {
    setNameWhenMissing(target.build(), target::setName, remoteName);
  }

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableBasicCredentials.Builder target,
      final @Context String remoteName) {
    setNameWhenMissing(target.build(), target::setName, remoteName);
  }

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableAzureCliCredentials.Builder target,
      final @Context String remoteName) {
    setNameWhenMissing(target.build(), target::setName, remoteName);
  }

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableAzureClientSecretCredentials.Builder target,
      final @Context String remoteName) {
    setNameWhenMissing(target.build(), target::setName, remoteName);
  }

  private void setNameWhenMissing(
      final Credentials credentials,
      final Function<String, ?> nameApplier,
      final @Nullable String remoteName) {
    if (Objects.nonNull(remoteName) && Optional.ofNullable(credentials.getName()).isEmpty()) {
      nameApplier.apply("inline[%s/%s]".formatted(remoteName, credentials.getType()));
    }
  }
}
