package co.uk.stefanpuia.backupr.config.model;

import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import jakarta.validation.Valid;
import java.util.List;

@Valid
public record BackuprConfig(List<@Valid ConfigRemote> remotes, List<@Valid ConfigSource> sources) {}
