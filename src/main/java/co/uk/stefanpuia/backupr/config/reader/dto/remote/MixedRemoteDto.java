package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

public record MixedRemoteDto(@Nullable String name, @Nullable @Valid ConfigRemoteDto remote) {}
