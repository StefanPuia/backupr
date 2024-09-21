package co.uk.stefanpuia.backupr.config.model.remote.credentials;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record BasicCredentials(@NotBlank String username, @Nullable String password) {}
