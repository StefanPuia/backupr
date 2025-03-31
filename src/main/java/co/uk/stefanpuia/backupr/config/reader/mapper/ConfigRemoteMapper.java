package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableAzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableGitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ImmutableLocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.RemoteType;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.AzureStorageBlobConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteWithCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.GitConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.LocalConfigRemoteDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = MapstructConfig.class,
    uses = {CoreDtoMapper.class, ConfigCredentialsMapper.class},
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigRemoteMapper {
  private static final NoneCredentials NONE_CREDENTIALS = NoneCredentials.create();
  @Autowired protected ConfigCredentialsMapper credentialsMapper;

  @Named("mapRemote")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @SubclassMapping(
      target = LocalConfigRemote.class,
      source = LocalConfigRemoteDto.class,
      qualifiedByName = "convertLocalConfigRemote")
  @SubclassMapping(
      target = GitConfigRemote.class,
      source = GitConfigRemoteDto.class,
      qualifiedByName = "convertGitConfigRemote")
  @SubclassMapping(
      target = AzureStorageBlobConfigRemote.class,
      source = AzureStorageBlobConfigRemoteDto.class,
      qualifiedByName = "convertAzureStorageBlobConfigRemote")
  protected abstract ConfigRemote mapRemote(
      ConfigRemoteDto source,
      @Nullable @Context String sourceName,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  @Named("convertLocalConfigRemote")
  @Mapping(target = "variables", ignore = true)
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "location", source = "location", qualifiedByName = "applyTemplateToString")
  protected abstract LocalConfigRemote convertLocalConfigRemote(
      LocalConfigRemoteDto source,
      @Nullable @Context String sourceName,
      @Context VariablesWrapper variables);

  @Named("convertGitConfigRemote")
  @Mapping(target = "variables", ignore = true)
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "url", source = "url", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "branch", source = "branch", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "credentials", source = "source")
  protected abstract GitConfigRemote convertGitConfigRemote(
      GitConfigRemoteDto source,
      @Nullable @Context String sourceName,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  @Named("convertAzureStorageBlobConfigRemote")
  @Mapping(target = "variables", ignore = true)
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "endpoint", source = "endpoint", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "container", source = "container", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "credentials", source = "source")
  protected abstract AzureStorageBlobConfigRemote convertAzureStorageBlobConfigRemote(
      AzureStorageBlobConfigRemoteDto source,
      @Nullable @Context String sourceName,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  protected Credentials mapMixedCredentials(
      final ConfigRemoteWithCredentialsDto remote,
      final @Context String sourceName,
      final @Context List<Credentials> credentials,
      final @Context VariablesWrapper variables) {
    final var source = remote.getCredentials();

    if (Objects.isNull(source)) {
      return NONE_CREDENTIALS;
    }

    if (Objects.isNull(source.credentials()) && Objects.isNull(source.name())) {
      return NONE_CREDENTIALS;
    }

    if (Objects.nonNull(source.credentials())) {
      return credentialsMapper.mapCredential(
          source.credentials(),
          Optional.ofNullable(remote.getName())
              .orElseGet(() -> this.getInlineName(remote.getType(), sourceName)),
          variables);
    }

    return credentials.stream()
        .filter(cred -> source.name().equals(cred.getName()))
        .findFirst()
        .orElseThrow(
            () ->
                new ConfigValidationException(
                    "in remote '%s': no credentials named '%s' defined"
                        .formatted(remote.getName(), source.name())));
  }

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableLocalConfigRemote.Builder target,
      final @Context String sourceName,
      final @Context VariablesWrapper variables) {
    target.setVariables(variables);
    setNameWhenMissing(target.build(), target::setName, sourceName);
  }

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableGitConfigRemote.Builder target,
      final @Context String sourceName,
      final @Context VariablesWrapper variables) {
    target.setVariables(variables);
    setNameWhenMissing(target.build(), target::setName, sourceName);
  }

  @AfterMapping
  protected void addContext(
      final @MappingTarget ImmutableAzureStorageBlobConfigRemote.Builder target,
      final @Context String sourceName,
      final @Context VariablesWrapper variables) {
    target.setVariables(variables);
    setNameWhenMissing(target.build(), target::setName, sourceName);
  }

  private void setNameWhenMissing(
      final ConfigRemote remote,
      final Function<String, ?> nameApplier,
      final @Nullable String sourceName) {
    if (Objects.nonNull(sourceName) && Optional.ofNullable(remote.getName()).isEmpty()) {
      nameApplier.apply(getInlineName(remote.getType(), sourceName));
    }
  }

  private String getInlineName(final RemoteType remoteType, final String sourceName) {
    return "inline[%s/%s]".formatted(sourceName, remoteType);
  }
}
