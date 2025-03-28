package co.uk.stefanpuia.backupr.config.model.remote.credentials;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface NoneCredentials extends Credentials {}
