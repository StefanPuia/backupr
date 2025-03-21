package co.uk.stefanpuia.backupr.config.model;

import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.util.List;

public record BackuprConfig(List<ConfigRemote> remotes, List<ConfigSource> sources) {}
