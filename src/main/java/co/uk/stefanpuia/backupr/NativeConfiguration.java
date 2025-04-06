package co.uk.stefanpuia.backupr;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Profile;

@Profile("native")
@Configuration
@ImportRuntimeHints(NativeConfiguration.AppRuntimeHints.class)
@RegisterReflectionForBinding()
public class NativeConfiguration {

  public static class AppRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {}
  }
}
