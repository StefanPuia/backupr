package co.uk.stefanpuia.backupr.config.model;

import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BackuprConfig(
    @NotEmpty List<@Valid ConfigRemote> remotes, @NotEmpty List<@Valid ConfigSource> sources) {}
