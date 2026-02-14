package co.uk.stefanpuia.backupr.config.reader.dto.cleanup;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

public record MixedCleanupDto(@Nullable String name, @Nullable @Valid CleanupDto cleanup) {}
