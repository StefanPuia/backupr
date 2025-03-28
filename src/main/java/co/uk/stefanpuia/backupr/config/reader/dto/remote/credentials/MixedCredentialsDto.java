package co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

public record MixedCredentialsDto(
    @Nullable String name, @Nullable @Valid CredentialsDto credentials) {}
