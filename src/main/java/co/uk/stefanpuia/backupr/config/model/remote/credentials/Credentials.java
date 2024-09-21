package co.uk.stefanpuia.backupr.config.model.remote.credentials;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

public record Credentials(@Valid @Nullable BasicCredentials basic) {}
