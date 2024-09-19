package co.uk.stefanpuia.backupr;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import com.azure.spring.cloud.autoconfigure.implementation.context.properties.AzureGlobalProperties;
import lombok.SneakyThrows;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Profile;

@Profile("native")
@Configuration
@Import({AzureGlobalProperties.class})
@ImportRuntimeHints(NativeConfiguration.RestRuntimeHints.class)
@RegisterReflectionForBinding({
  BackuprConfig.class,
  LocalConfigSource.class,
  AzureStorageConfigRemote.class,
  LocalConfigRemote.class
})
public class NativeConfiguration {

  public static class RestRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    @SneakyThrows
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
      // hints
      //     .reflection()
      //     .registerConstructor(
      //         BackuprConfig.class.getDeclaredConstructor(List.class, List.class),
      //         ExecutableMode.INVOKE);
    }
  }
}
