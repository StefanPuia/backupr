package co.uk.stefanpuia.backupr.config.reader.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import co.uk.stefanpuia.backupr.config.model.state.ImmutableBackupState;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.MixedRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.state.StateDto;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigStateMapper {
  private final ConfigRemoteMapper remoteMapper;

  public BackupState mapState(
      @Nullable final StateDto state,
      final List<Credentials> credentials,
      final List<ConfigRemote> remotes,
      final VariablesWrapper variables) {
    if (Objects.isNull(state)) {
      return null;
    }
    return ImmutableBackupState.of(
        mapMixedRemote(state.getRemote(), remotes, credentials, variables));
  }

  private ConfigRemote mapMixedRemote(
      final MixedRemoteDto remote,
      final @Context List<ConfigRemote> remotes,
      final @Context List<Credentials> credentials,
      final @Context VariablesWrapper variables) {
    if (isNull(remote.name()) && isNull(remote.remote())) {
      throw new ConfigValidationException(
          "in source '%s': either a name or inline remote must be provided");
    }
    if (nonNull(remote.name())) {
      return pickRemote(remotes, remote.name());
    }
    return remoteMapper.mapRemote(remote.remote(), null, credentials, List.of(), variables);
  }

  private ConfigRemote pickRemote(
      final List<ConfigRemote> remotes, final @NotNull String remoteName) {
    return remotes.stream()
        .filter(remote -> remoteName.equals(remote.getName()))
        .findFirst()
        .orElseThrow(
            () ->
                new ConfigValidationException(
                    "in state config: no remote named '%s' defined".formatted(remoteName)));
  }
}
